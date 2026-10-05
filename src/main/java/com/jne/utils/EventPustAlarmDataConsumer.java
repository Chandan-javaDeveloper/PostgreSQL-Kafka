package com.jne.utils;





import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.EventPushDataSinglePhase;
import com.jne.model.Outage;
import com.jne.repo.EventPushDataSinglePhaseRepo;
import com.jne.repo.OutageRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class EventPustAlarmDataConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventPustAlarmDataConsumer.class);

    // ---------------- Queue ----------------
    private final ConcurrentLinkedQueue<EventPushDataSinglePhase> queue = new ConcurrentLinkedQueue<>();

    // ---------------- Config ----------------
    @Value("${kafka.alarm.group-id}")
    private String groupId;

    @Value("${feature.queue-writer.enabled:true}")
    private boolean queueWriterEnabled;

    @Value("${feature.queue.batch-size:1000}")
    private int QUEUE_BATCH;

    @Value("${feature.queue.flush-ms:500}")
    private long QUEUE_FLUSH_MS;

    @Value("${feature.queue.concurrency:4}")
    private int QUEUE_CONCURRENCY;

    // ---- Flow control ----
    private static final int MAX_RETRY       = 3;
    private static final long RETRY_DELAY_MS = 5000L;

    private static final int MAX_PENDING_INSERTS = 20000;
    private static final int RESUME_THRESHOLD    = 10000;
    private static final int MAX_QUEUE_SIZE      = 25000;

    private static final int WRITE_CONCURRENCY = 16;
    private static final int BUFFER_SIZE       = 2000;

    private final AtomicInteger pendingInserts = new AtomicInteger(0);
    private final AtomicBoolean paused         = new AtomicBoolean(false);

    @Autowired private KafkaCommandMetrics commandMetrics;
    @Autowired private KafkaListenerEndpointRegistry registry;
    
    @Autowired
    private EventPushDataSinglePhaseRepo eventPushDataRepo;

    @Autowired
    private OutageRepo outageRepo;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ThreadLocal<SimpleDateFormat> DF =
        ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            return sdf;
        });

    private ExecutorService workers;

    // --------------------------------------------------
    // Startup / Shutdown
    // --------------------------------------------------
    @PostConstruct
    public void startWorkers() {
        if (!queueWriterEnabled) {
            log.info("ℹ Alarm Cassandra writers disabled");
            return;
        }

        workers = Executors.newFixedThreadPool(
            QUEUE_CONCURRENCY, r -> new Thread(r, "alarm-cassandra-writer"));

        for (int i = 0; i < QUEUE_CONCURRENCY; i++) {
            workers.submit(this::workerLoop);
        }

        log.info("✅ Alarm Cassandra writers started");
    }

    @PreDestroy
    public void stopWorkers() {
        if (workers != null) workers.shutdownNow();
    }

    // --------------------------------------------------
    // Worker loop
    // --------------------------------------------------
    private void workerLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                drainAndSaveBatch(QUEUE_BATCH).block();
                Thread.sleep(QUEUE_FLUSH_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                log.error("❌ Alarm Cassandra worker crashed", t);
            }
        }
    }

    // --------------------------------------------------
    // Kafka Consumer
    // --------------------------------------------------
    @KafkaListener(
        id = "AlarmEventPushCassandra",
        topics = "alarm-data",
        groupId = "${kafka.alarm.group-id}",
        concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {

        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        Flux.fromIterable(messages)
            .flatMap(msg -> {
                try {
                    if (msg == null || msg.isBlank()) return Mono.empty();
                    return Mono.just(objectMapper.readTree(msg));
                } catch (Exception e) {
                    log.warn("⚠ Invalid JSON skipped");
                    return Mono.empty();
                }
            }, WRITE_CONCURRENCY)
            .flatMap(this::processRoot, WRITE_CONCURRENCY)
            .onBackpressureBuffer(
                BUFFER_SIZE,
                d -> log.warn("⚠ Alarm Cassandra buffer full, dropping"),
                BufferOverflowStrategy.DROP_OLDEST
            )
            .then()
            .doOnSuccess(v -> ack.acknowledge())
            .subscribe();
    }

    // --------------------------------------------------
    // JSON → Entity
    // --------------------------------------------------
    private Flux<EventPushDataSinglePhase> processRoot(JsonNode root) {

        final String meterNo    = safe(root.path("meterNo").asText(null));
        final String trackingId = safe(root.path("trackingId").asText(null));
        final String obisCmd    = safe(root.path("obisCmd").asText(null));

        final LocalDateTime mdasTime =
                parseDateTime(root.path("systemTime").asText(null));
        
        JsonNode dataNode = root.path("data");
        if (!dataNode.isObject()) return Flux.empty();

        JsonNode headers = dataNode.path("1");
        JsonNode row     = dataNode.path("2");

        // alarm-data format: 6 fields expected
        if (!headers.isArray() || !row.isArray() || row.size() < 6) {
            return Flux.empty();
        }

        // indexes (based on your kafka sample):
        // 0 Alarm Date Time
        // 1 Alarm code
        // 2 trigger
        // 3 level  (OPEN)
        // 4 push Data
        // 5 Event type (ex: "OVER VOLTAGE|...|FIRST BREATH|")
        final String alarmDatetimeStr = safe(row.get(0).asText(null));
        final String alarmCode        = safe(row.get(1).asText(null));
        final String levelOpenClose   = safe(row.get(3).asText(null)); // "OPEN"
        final String pushBits         = safe(row.get(4).asText(null));
        final String eventType        = safe(row.get(5).asText(null));

        final LocalDateTime alarmDatetime =
                parseDateTime(alarmDatetimeStr);
        
        if (meterNo == null || meterNo.isBlank() || alarmDatetime == null) {
            log.warn("⚠ Skipping invalid alarm record meter={} datetime={}", meterNo, alarmDatetime);
            return Flux.empty();
        }

        // ---------------- Existing EventPushData (UNCHANGED) ----------------
     // ---------------- EventPushData ----------------
        EventPushDataSinglePhase e = new EventPushDataSinglePhase();

        e.setDeviceSerialNumber(meterNo);
        e.setCommandType(obisCmd);
        e.setData(pushBits);
        e.setEventStatus(eventType);
        e.setTrackingId(trackingId);
        e.setOwnerName(ObisFieldMapping.resolveOwner(meterNo));
        e.setMeterDatetime(alarmDatetime);
        e.setMdasDatetime(mdasTime);
        e.setReadingType(1);

        // queue for batch PostgreSQL save
        guardAndEnqueue(e);

        // ---------------- OUTAGE SAVE ----------------
        if (isOutageAlarm(alarmCode)) {

            String outageCommandType =
                    resolveBreathCommandType(alarmCode, eventType);

            try {

            	Outage outage = new Outage();

            	outage.setDeviceSerialNumber(meterNo);
            	outage.setCommandType(outageCommandType);

            	outage.setMeterDatetime(alarmDatetime);
            	outage.setMdasDatetime(mdasTime);
                e.setReadingType(1);


            	outage.setEventStatus(eventType);
            	outage.setData(pushBits);
            	outage.setGroups(levelOpenClose);
            	outage.setTrackingId(trackingId);
            	outage.setOwnerName(
            	        ObisFieldMapping.resolveOwner(meterNo));

            	commandMetrics.incrementConsumed(
            	        "Outage-PostgreSQL",
            	        1);

            //	outageRepo.save(outage);
            	
            	outageRepo.insertIgnore(
            	        outage.getDeviceSerialNumber(),
            	        outage.getMeterDatetime(),
            	        outage.getCommandType(),
            	        outage.getData(),
            	        outage.getEventStatus(),
            	        outage.getGroups(),
            	        outage.getMdasDatetime(),
            	        outage.getOwnerName(),
            	        outage.getTrackingId()
            	);

            	commandMetrics.incrementSaved(
            	        "Outage-PostgreSQL",
            	        1);

                log.info(
                        "✅ OUTAGE saved meter={} alarmCode={} commandType={} groups={}",
                        meterNo,
                        alarmCode,
                        outageCommandType,
                        levelOpenClose);

            } catch (Exception ex) {

                log.error(
                        "❌ OUTAGE save failed meter={} err={}",
                        meterNo,
                        ex.getMessage(),
                        ex);
            }
        }

        return Flux.just(e);
    }

    // --------------------------------------------------
    // Drain & Save
    // --------------------------------------------------
    @Transactional
    private Mono<Integer> drainAndSaveBatch(int max) {

        List<EventPushDataSinglePhase> batch = new ArrayList<>(max);

        for (int i = 0; i < max; i++) {
            EventPushDataSinglePhase e = queue.poll();
            if (e == null) {
                break;
            }
            batch.add(e);
        }

        if (batch.isEmpty()) {
            return Mono.just(0);
        }

        try {

            commandMetrics.incrementConsumed(
                    "Alarm-PostgreSQL",
                    batch.size());

          //  eventPushDataRepo.saveAll(batch);
            int inserted = 0;

            for (EventPushDataSinglePhase e : batch) {

                inserted += eventPushDataRepo.insertIgnore(
                        e.getDeviceSerialNumber(),
                        e.getMeterDatetime(),
                        e.getCommandType(),
                        e.getData(),
                        e.getEventStatus(),
                        e.getGroups(),
                        e.getMdasDatetime(),
                        e.getOwnerName(),
                        e.getTrackingId()
                );
            }

            commandMetrics.incrementSaved(
                    "Alarm-PostgreSQL",
                    inserted);

            return Mono.just(batch.size());

        } catch (Exception ex) {

            log.error(
                    "❌ PostgreSQL batch insert failed size={} err={}",
                    batch.size(),
                    ex.getMessage(),
                    ex);

            batch.forEach(queue::offer);

            forcePause();

            return Mono.just(0);

        } finally {

            pendingInserts.addAndGet(-batch.size());
        }
    }

    // --------------------------------------------------
    // Backpressure control
    // --------------------------------------------------
    private void guardAndEnqueue(EventPushDataSinglePhase e) {
        if (queue.size() > MAX_QUEUE_SIZE) {
            forcePause();
            return;
        }
        queue.offer(e);
        pendingInserts.incrementAndGet();
//        commandMetrics.incrementConsumed("Alarm-Cassandra", 1);
    }

    @Scheduled(fixedDelay = 1000)
    public void monitorBacklog() {
        MessageListenerContainer c =
            registry.getListenerContainer("AlarmEventPushCassandra");
        if (c == null) return;

        int backlog = pendingInserts.get() + queue.size();

        if (backlog > MAX_PENDING_INSERTS && paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing Alarm Cassandra consumer backlog={}", backlog);
            c.pause();
        } else if (backlog < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
            log.info("▶ Resuming Alarm Cassandra consumer");
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c =
            registry.getListenerContainer("AlarmEventPushCassandra");
        if (c != null && paused.compareAndSet(false, true)) {
            c.pause();
        }
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------
    

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private boolean isOutageAlarm(String alarmCode) {
        return "409011".equals(alarmCode) || "409012".equals(alarmCode);
    }

    
    private String resolveBreathCommandType(String alarmCode, String eventType) {
        String et = (eventType == null) ? "" : eventType.toUpperCase();

        if (et.contains("LAST GASP")) return "LAST GASP";
        if (et.contains("FIRST BREATH")) return "FIRST BREATH";

        // fallback mapping (if eventType text is missing)
        if ("409011".equals(alarmCode)) return "FIRST BREATH";
        if ("409012".equals(alarmCode)) return "LAST GASP";

        return "OUTAGE";
    }
    
    
    
    private LocalDateTime parseDateTime(String s) {

        try {

            if (s == null || s.isBlank()) {
                return null;
            }

            Date date = DF.get().parse(s.trim());

            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

        } catch (Exception e) {

            return null;
        }
    }
    
    
}
