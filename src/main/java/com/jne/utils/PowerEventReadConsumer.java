package com.jne.utils;


import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.EventDataSinglePhase;
import com.jne.model.EventDataSinglePhaseId;
import com.jne.model.EventDataThreePhase;
import com.jne.model.EventDataThreePhaseId;
import com.jne.repo.EventDataSinglePhaseRepo;
import com.jne.repo.EventDataThreePhaseRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class PowerEventReadConsumer {

	 private static final Logger log = LoggerFactory.getLogger(PowerEventReadConsumer.class);
	    private static final ObjectMapper objectMapper = new ObjectMapper();

	    // ---------------- Config ----------------
	    @Value("${kafka.powerevent.group-id}")
	    private String groupId;

	    @Value("${feature.queue-writer.enabled:true}")
	    private boolean queueWriterEnabled;

	    @Value("${feature.queue.batch-size:2000}")
	    private int QUEUE_BATCH;

	    @Value("${feature.queue.flush-ms:10}")
	    private long QUEUE_FLUSH_MS;

	    @Value("${feature.queue.concurrency:4}")
	    private int QUEUE_CONCURRENCY;

	    // parsing parallelism
	    private static final int WRITE_CONCURRENCY = 32;
	    private static final int BUFFER_SIZE       = 2000;

	    // queue limits
	    private static final int MAX_QUEUE_SIZE = 25000;
	    private static final int RESUME_WATERMARK = MAX_QUEUE_SIZE / 5;

	    private static final long RETRY_DELAY_MS = 500; // faster retry for PG

	    private final AtomicBoolean paused = new AtomicBoolean(false);

	    // ---------------- Queues (bounded, NO LOSS) ----------------
	    private final BlockingQueue<EventDataSinglePhase> spQueue =
	            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

	    private final BlockingQueue<EventDataThreePhase> tpQueue =
	            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

	    // ✅ FAST WRITER POOLS
	    private ExecutorService spWriters;
	    private ExecutorService tpWriters;

	    private final EventDataSinglePhaseRepo spRepo;
	    private final EventDataThreePhaseRepo tpRepo;
	    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	    private final KafkaCommandMetrics commandMetrics;

	    public PowerEventReadConsumer(
	            EventDataSinglePhaseRepo spRepo,
	            EventDataThreePhaseRepo tpRepo,
	            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
	            KafkaCommandMetrics commandMetrics
	    ) {
	        this.spRepo = spRepo;
	        this.tpRepo = tpRepo;
	        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
	        this.commandMetrics = commandMetrics;
	    }

	    // ----------- Datetime parsing (safe) -----------
	    private static final List<DateTimeFormatter> DT_FORMATS = List.of(
	            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
	            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
	            DateTimeFormatter.ISO_LOCAL_DATE_TIME
	    );

	    private LocalDateTime parseDateTimeOrNow(String raw) {
	        if (raw == null || raw.isBlank()) return LocalDateTime.now();
	        String cleaned = raw
	                .replace('T', ' ')
	                .replaceAll("[^0-9:\\-\\. ]", " ")
	                .replaceAll("\\s+", " ")
	                .trim();

	        for (DateTimeFormatter f : DT_FORMATS) {
	            try { return LocalDateTime.parse(cleaned, f); }
	            catch (Exception ignored) {}
	        }
	        return LocalDateTime.now();
	    }

	    // ---------------------------------------------------------
	    // START / STOP WRITERS (✅ FAST)
	    // ---------------------------------------------------------
	    @PostConstruct
	    public void startWriters() {
	        if (!queueWriterEnabled) {
	            log.warn("⚠ queue-writer disabled; will still enqueue but writers OFF -> will look stuck.");
	        }

	        int writers = Math.max(1, QUEUE_CONCURRENCY);

	        spWriters = Executors.newFixedThreadPool(
	                writers,
	                r -> new Thread(r, "PowerEvents-pg-sp-writer")
	        );
	        tpWriters = Executors.newFixedThreadPool(
	                writers,
	                r -> new Thread(r, "PowerEvents-pg-tp-writer")
	        );

	        // ✅ start loops
	        for (int i = 0; i < writers; i++) {
	            spWriters.submit(this::spWriterLoop);
	            tpWriters.submit(this::tpWriterLoop);
	        }

	        log.info("✅ PowerEvents(PG) writers started (writers={}, batch={}, flush={}ms)",
	                writers, QUEUE_BATCH, QUEUE_FLUSH_MS);
	    }

	    @PreDestroy
	    public void stopWriters() {
	        try { if (spWriters != null) spWriters.shutdownNow(); } catch (Exception ignore) {}
	        try { if (tpWriters != null) tpWriters.shutdownNow(); } catch (Exception ignore) {}
	    }

	    private void spWriterLoop() {
	        while (!Thread.currentThread().isInterrupted()) {
	            try {
	                int wrote = drainAndSaveSPBatch(QUEUE_BATCH);
	                if (wrote == 0) Thread.sleep(QUEUE_FLUSH_MS);
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt();
	            } catch (Throwable t) {
	                log.error("❌ SP writer crashed (continuing)", t);
	                sleepQuiet(200);
	            }
	        }
	    }

	    private void tpWriterLoop() {
	        while (!Thread.currentThread().isInterrupted()) {
	            try {
	                int wrote = drainAndSaveTPBatch(QUEUE_BATCH);
	                if (wrote == 0) Thread.sleep(QUEUE_FLUSH_MS);
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt();
	            } catch (Throwable t) {
	                log.error("❌ TP writer crashed (continuing)", t);
	                sleepQuiet(200);
	            }
	        }
	    }

    // ---------------------------------------------------------
    // KAFKA LISTENER (BATCH)
    // ---------------------------------------------------------
    @KafkaListener(
            id = "PowerRelatedEventsListener",
            topics = "PowerRelatedEvents",
            groupId = "${kafka.powerevent.group-id}",
            concurrency = "${feature.queue.concurrency:6}"
        )
    public void consume(List<String> messages, Acknowledgment ack) {

        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        Flux.fromIterable(messages)
            .flatMap(msg -> Mono.fromCallable(() -> {
                if (msg == null || msg.isBlank()) return null;
                return objectMapper.readTree(msg);
            }).onErrorResume(e -> Mono.empty()), WRITE_CONCURRENCY)
            .filter(r -> r != null)
            .flatMap(this::parseBuildAndEnqueue, WRITE_CONCURRENCY)
            .onBackpressureBuffer(
                BUFFER_SIZE,
                dropped -> forcePause(),
                BufferOverflowStrategy.DROP_OLDEST
            )
            .then()
            .doOnSuccess(v -> ack.acknowledge())
            .doOnError(err -> {
                log.error("❌ PowerEvents(PG) pipeline error, NOT acking", err);
                forcePause();
            })
            .subscribe();
    }

    private Mono<Void> parseBuildAndEnqueue(JsonNode root) {
        return Mono.fromRunnable(() -> {
            String meterType  = root.path("meterType").asText(null);
            String meterNo    = root.path("meterNo").asText(null);
            String systemTime = root.path("systemTime").asText(null);

            if (meterNo == null || meterNo.isBlank()) return;

            LocalDateTime mdasDateTime = parseDateTimeOrNow(systemTime);

            JsonNode dataNode = root.path("data");
            if (!(dataNode.isArray() && dataNode.size() > 0)) return;

            JsonNode obisData = dataNode.get(0);

            List<String> obisCodes;
            try {
                obisCodes = objectMapper.convertValue(
                        obisData.path("1"),
                        new TypeReference<List<String>>() {}
                );
            } catch (Exception e) {
                return;
            }
            if (obisCodes == null || obisCodes.isEmpty()) return;

            List<String> fieldKeys = new ArrayList<>();
            obisData.fieldNames().forEachRemaining(fk -> {
                if (!"1".equals(fk)) fieldKeys.add(fk);
            });

            if ("Single Phase".equalsIgnoreCase(meterType)) {
                for (String fk : fieldKeys) {
                    try {
                        List<Object> obisValues = objectMapper.convertValue(
                                obisData.path(fk),
                                new TypeReference<List<Object>>() {}
                        );
                        EventDataSinglePhase e = buildSPEntity(meterNo, mdasDateTime, obisCodes, obisValues);
                        if (e != null) guardAndEnqueueSP(e);
                    } catch (Exception ex) {
                        log.error("❌ SP block failed {}: {}", fk, ex.toString());
                    }
                }
            } else if (meterType != null && (
                    meterType.equalsIgnoreCase("Three Phase")
                 || meterType.equalsIgnoreCase("CT Meter")
                 || meterType.equalsIgnoreCase("HT Meter"))) {

                for (String fk : fieldKeys) {
                    try {
                        List<Object> obisValues = objectMapper.convertValue(
                                obisData.path(fk),
                                new TypeReference<List<Object>>() {}
                        );
                        EventDataThreePhase e = buildTPEntity(meterNo, mdasDateTime, obisCodes, obisValues);
                        if (e != null) guardAndEnqueueTP(e);
                    } catch (Exception ex) {
                        log.error("❌ TP block failed {}: {}", fk, ex.toString());
                    }
                }
            } else {
                log.warn("⚠ Unknown meterType: {}", meterType);
            }
        });
    }

    // ---------------------------------------------------------
    // FAST DRAIN & SAVE (blocking JPA)
    // ---------------------------------------------------------
    private int drainAndSaveSPBatch(int maxBatch) {
        List<EventDataSinglePhase> batch = new ArrayList<>(maxBatch);
        spQueue.drainTo(batch, maxBatch);
        if (batch.isEmpty()) return 0;

        try {
            spRepo.saveAll(batch);
            // if repo supports flush:
            // spRepo.flush();

            commandMetrics.incrementSaved("PowerEventData-1P", batch.size());
            return batch.size();

        } catch (DataIntegrityViolationException badBatch) {
            // isolate bad rows
            int ok = 0;
            for (EventDataSinglePhase e : batch) {
                try {
                    spRepo.save(e);
                    ok++;
                } catch (DataIntegrityViolationException perm) {
                    // permanent bad/duplicate -> DROP (do not requeue)
                } catch (Exception transientErr) {
                    requeueSP(e); // transient -> requeue
                }
            }
            if (ok > 0) commandMetrics.incrementSaved("PowerEventData-1P", ok);
            return ok;

        } catch (Exception ex) {
            log.error("❌ SP saveAll failed (requeue {}): {}", batch.size(), ex.toString());
            batch.forEach(this::requeueSP);
            forcePause();
            sleepQuiet(RETRY_DELAY_MS);
            return 0;
        }
    }

    private int drainAndSaveTPBatch(int maxBatch) {
        List<EventDataThreePhase> batch = new ArrayList<>(maxBatch);
        tpQueue.drainTo(batch, maxBatch);
        if (batch.isEmpty()) return 0;

        try {
            tpRepo.saveAll(batch);
            // tpRepo.flush();

            commandMetrics.incrementSaved("PowerEventData-3P", batch.size());
            return batch.size();

        } catch (DataIntegrityViolationException badBatch) {
            int ok = 0;
            for (EventDataThreePhase e : batch) {
                try {
                    tpRepo.save(e);
                    ok++;
                } catch (DataIntegrityViolationException perm) {
                    // DROP
                } catch (Exception transientErr) {
                    requeueTP(e);
                }
            }
            if (ok > 0) commandMetrics.incrementSaved("PowerEventData-3P", ok);
            return ok;

        } catch (Exception ex) {
            log.error("❌ TP saveAll failed (requeue {}): {}", batch.size(), ex.toString());
            batch.forEach(this::requeueTP);
            forcePause();
            sleepQuiet(RETRY_DELAY_MS);
            return 0;
        }
    }

    private void sleepQuiet(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    private void requeueSP(EventDataSinglePhase e) {
        try { spQueue.put(e); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    private void requeueTP(EventDataThreePhase e) {
        try { tpQueue.put(e); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    // ---------------------------------------------------------
    // Pause / Resume (queue based)
    // ---------------------------------------------------------
    @Scheduled(fixedDelay = 1000)
    public void monitorAndThrottle() {
        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("PowerRelatedEventsListener");
        if (c == null) return;

        int q = spQueue.size() + tpQueue.size();

        if (q >= MAX_QUEUE_SIZE && paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing PowerRelatedEventsListener (queue={} >= {})", q, MAX_QUEUE_SIZE);
            c.pause();
            return;
        }

        if (q <= RESUME_WATERMARK && paused.compareAndSet(true, false)) {
            log.info("▶ Resuming PowerRelatedEventsListener (queue={} <= {})", q, RESUME_WATERMARK);
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("PowerRelatedEventsListener");
        if (c != null && paused.compareAndSet(false, true)) {
            c.pause();
        }
    }

    // ---------------------------------------------------------
    // Enqueue helpers (NO LOSS)
    // ---------------------------------------------------------
    private void guardAndEnqueueSP(EventDataSinglePhase e) {
        if (spQueue.remainingCapacity() == 0) forcePause();
        try { spQueue.put(e); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        commandMetrics.incrementConsumed("PowerEventData-1P", 1);
    }

    private void guardAndEnqueueTP(EventDataThreePhase e) {
        if (tpQueue.remainingCapacity() == 0) forcePause();
        try { tpQueue.put(e); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        commandMetrics.incrementConsumed("PowerEventData-3P", 1);
    }

    // ---------------------------------------------------------
    // Entity builders (same as yours)
    // ---------------------------------------------------------
    private EventDataSinglePhase buildSPEntity(
            String meterNo,
            LocalDateTime mdasDateTime,
            List<String> obisCodes,
            List<Object> obisValues
    ) {
        LocalDateTime eventDt = extractEventDatetimeFromObis(obisCodes, obisValues, mdasDateTime);

        EventDataSinglePhase entity = new EventDataSinglePhase();

        EventDataSinglePhaseId id = new EventDataSinglePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setEventDatetime(eventDt);
        entity.setId(id);

        entity.setMdasDatetime(mdasDateTime);
        entity.setEventCategory("PowerEvents");
        entity.setReadingType(1);


        Map<String, String> mapping = ObisFieldMapping.getEventsFieldMappingSinglePhase();
        mapObisDataToEntity(entity, obisCodes, obisValues, mapping);
        
        populateEventType(entity);

        if (entity.getId() == null || entity.getId().getDeviceSerialNumber() == null
                ||  entity.getId().getEventDatetime() == null) {
            return null;
        }
        return entity;
    }

    private EventDataThreePhase buildTPEntity(
            String meterNo,
            LocalDateTime mdasDateTime,
            List<String> obisCodes,
            List<Object> obisValues
    ) {
        LocalDateTime eventDt = extractEventDatetimeFromObis(obisCodes, obisValues, mdasDateTime);

        EventDataThreePhase entity = new EventDataThreePhase();

        EventDataThreePhaseId id = new EventDataThreePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setEventDatetime(eventDt);
        entity.setId(id);

        entity.setMdasDatetime(mdasDateTime);
        entity.setEventCategory("PowerEvents");
        entity.setReadingType(1);

        Map<String, String> mapping = ObisFieldMapping.getEventsFieldMappingThreePhase();
        mapObisDataToEntity(entity, obisCodes, obisValues, mapping);

        populateEventType(entity);
        
        if (entity.getId() == null || entity.getId().getDeviceSerialNumber() == null
                || entity.getId().getEventDatetime() == null) {
            return null;
        }
        return entity;
    }

    private LocalDateTime extractEventDatetimeFromObis(
            List<String> obisCodes,
            List<Object> obisValues,
            LocalDateTime fallback
    ) {
        if (obisCodes == null || obisValues == null) return fallback;

        int n = Math.min(obisCodes.size(), obisValues.size());
        for (int i = 0; i < n; i++) {
            if ("0.0.1.0.0.255".equals(obisCodes.get(i))) {
                Object v = obisValues.get(i);
                if (v == null) return fallback;
                String s = String.valueOf(v).trim();
                if (s.isEmpty() || "null".equalsIgnoreCase(s)) return fallback;
                return parseDateTimeOrNow(s);
            }
        }
        return fallback;
    }

    private void mapObisDataToEntity(
            Object entity,
            List<String> obisCodes,
            List<Object> obisValues,
            Map<String, String> mapping
    ) {
        if (obisCodes == null || obisValues == null || mapping == null) return;
        int size = Math.min(obisCodes.size(), obisValues.size());

        for (int i = 0; i < size; i++) {
            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);
            if (value == null || "null".equalsIgnoreCase(String.valueOf(value))) continue;

            String fieldName = mapping.get(obis);
            if (fieldName == null) continue;

            try {
                Field field = entity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Class<?> t = field.getType();

                if (t == LocalDateTime.class) {
                    field.set(entity, parseDateTimeOrNow(String.valueOf(value)));
                } else if (t == LocalDate.class) {
                    field.set(entity, parseDateTimeOrNow(String.valueOf(value)).toLocalDate());
                } else if (t == Double.class || t == double.class) {
                    field.set(entity, Double.parseDouble(String.valueOf(value)));
                } else if (t == Integer.class || t == int.class) {
                    field.set(entity, Integer.parseInt(String.valueOf(value)));
                } else if (t == Long.class || t == long.class) {
                    field.set(entity, Long.parseLong(String.valueOf(value)));
                } else {
                    field.set(entity, String.valueOf(value));
                }
            } catch (NoSuchFieldException nf) {
                // ignore
            } catch (Exception e) {
                log.error("❌ Mapping failed for OBIS {} => {}", obis, e.toString());
            }
        }
    }
    
    private void populateEventType(EventDataSinglePhase entity) {

        if (entity.getEventCode() == null) {
            return;
        }

        entity.setEventType(
                ObisFieldMapping.getEventTypeMapping()
                        .getOrDefault(
                                String.valueOf(entity.getEventCode()),
                                "Unknown Event"
                        )
        );
    }

    private void populateEventType(EventDataThreePhase entity) {

        if (entity.getEventCode() == null) {
            return;
        }

        entity.setEventType(
                ObisFieldMapping.getEventTypeMapping()
                        .getOrDefault(
                                String.valueOf(entity.getEventCode()),
                                "Unknown Event"
                        )
        );
    }
}