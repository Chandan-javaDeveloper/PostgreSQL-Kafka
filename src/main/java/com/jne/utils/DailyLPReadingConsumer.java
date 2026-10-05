package com.jne.utils;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.DailyLoadProfileSinglePhase;
import com.jne.model.DailyLoadProfileSinglePhaseId;
import com.jne.model.DailyLoadProfileThreePhase;
import com.jne.model.DailyLoadProfileThreePhaseId;
import com.jne.repo.DailyLoadProfileSinglePhaseRepo;
import com.jne.repo.DailyLoadProfileThreePhaseRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class DailyLPReadingConsumer {

    private static final Logger log = LoggerFactory.getLogger(DailyLPReadingConsumer.class);

    @Value("${feature.queue-writer.enabled:true}")
    private boolean queueWriterEnabled;

    @Value("${feature.queue.batch-size:1000}")
    private int QUEUE_BATCH;

    @Value("${feature.queue.flush-ms:200}")
    private long QUEUE_FLUSH_MS;

    @Value("${feature.queue.concurrency:4}")
    private int QUEUE_CONCURRENCY;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // retry / flow
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 5000L;
    private static final int WRITE_CONCURRENCY = 32;
    private static final int BUFFER_SIZE = 2000;

    // backlog limits
    private static final int MAX_PENDING_INSERTS = 20000;
    private static final int RESUME_THRESHOLD = 10000;
    private static final int MAX_QUEUE_SIZE = 25000;
    
    private static final String DT_OBIS = "0.0.1.0.0.255";


    private final AtomicInteger pendingInserts = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    private final BlockingQueue<DailyLoadProfileSinglePhase> spQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final BlockingQueue<DailyLoadProfileThreePhase> tpQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final ScheduledExecutorService drainScheduler =
            Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "dailylp-pg-drain"));

    
    // O(1) queue size counters (avoid queue.size() O(n))
    private final AtomicInteger spQSize = new AtomicInteger(0);
    private final AtomicInteger tpQSize = new AtomicInteger(0);



    private final DailyLoadProfileSinglePhaseRepo spRepo;
    private final DailyLoadProfileThreePhaseRepo tpRepo;
    private final KafkaListenerEndpointRegistry registry;
    private final KafkaCommandMetrics commandMetrics;
    private final TransactionTemplate tx;



    public DailyLPReadingConsumer(
            DailyLoadProfileSinglePhaseRepo spRepo,
            DailyLoadProfileThreePhaseRepo tpRepo,
            KafkaListenerEndpointRegistry registry,
            KafkaCommandMetrics commandMetrics,
            PlatformTransactionManager txManager
    ) {
        this.spRepo = spRepo;
        this.tpRepo = tpRepo;
        this.registry = registry;
        this.commandMetrics = commandMetrics;
        this.tx = new TransactionTemplate(txManager);
    }


    private static final ThreadLocal<SimpleDateFormat> DF =
        ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            return sdf;
        });

  
    // ---------------------------------------------------------
    // KAFKA LISTENER
    // ---------------------------------------------------------
    @KafkaListener(
        id = "DailyLoadProfile",
        topics = "DailyLoadProfile",
        groupId = "${kafka.dailyLp.group-id}",
        concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {

        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        Flux.fromIterable(messages)
            .flatMap(msg -> Mono.fromCallable(() -> {
                if (msg == null || msg.isBlank()) throw new IllegalArgumentException("Empty Kafka message");
                return objectMapper.readTree(msg);
            }), WRITE_CONCURRENCY)
            .flatMap(root -> {

                String meterType  = root.path("meterType").asText(null);
                String meterNo    = root.path("meterNo").asText(null);
                String systemTime = root.path("systemTime").asText(null);

                Date mdasDateTime = parseDateOrNow(systemTime);

                JsonNode dataNode = root.path("data");
                if (!(dataNode.isArray() && dataNode.size() > 0)) return Flux.empty();

                JsonNode obisData = dataNode.get(0);

                List<String> obisCodes = objectMapper.convertValue(
                        obisData.path("1"),
                        new TypeReference<List<String>>() {}
                );

                List<String> fieldKeys = new ArrayList<>();
                obisData.fieldNames().forEachRemaining(fieldKeys::add);

                if ("Single Phase".equalsIgnoreCase(meterType)) {
                    return processSinglePhase(meterNo, mdasDateTime, obisCodes, fieldKeys, obisData);
                } else if (meterType != null && (
                        meterType.equalsIgnoreCase("Three Phase")
                     || meterType.equalsIgnoreCase("CT Meter")
                     || meterType.equalsIgnoreCase("HT Meter"))) {
                    return processThreePhase(meterNo, mdasDateTime, obisCodes, fieldKeys, obisData);
                } else {
                    log.warn("⚠ Unknown meterType in DailyLP: {}", meterType);
                    return Flux.empty();
                }

            }, WRITE_CONCURRENCY)
            .onBackpressureBuffer(
                BUFFER_SIZE,
                dropped -> log.warn("⚠ DailyLP PG buffer full — dropped chunk."),
                BufferOverflowStrategy.DROP_OLDEST
            )
            .then()
            .retryWhen(reactor.util.retry.Retry.fixedDelay(MAX_RETRY, Duration.ofMillis(RETRY_DELAY_MS)))
            .doOnError(err -> log.error("❌ DailyLP PG pipeline error", err))
            .subscribe(
            		   v -> {},
            		   err -> log.error("❌ DailyLP consume pipeline error", err),
            		   ack::acknowledge
            		);
    }
    
    @PostConstruct
    public void startDrainLoop() {
        drainScheduler.scheduleWithFixedDelay(
            this::drainAndSave,
            1, 1, TimeUnit.SECONDS
        );
    }

    // ---------------------------------------------------------
    // Build → Enqueue
    // ---------------------------------------------------------
    private Flux<DailyLoadProfileSinglePhase> processSinglePhase(
            String meterNo,
            Date mdasDateTime,
            List<String> obisCodes,
            List<String> fieldKeys,
            JsonNode obisData
    ) {
        List<DailyLoadProfileSinglePhase> entities = new ArrayList<>();

        // ✅ convert Date -> LocalDateTime once
        LocalDateTime mdasLdt = toLdt(mdasDateTime);

        for (String fk : fieldKeys) {
            if ("1".equals(fk)) continue;

            try {
                List<Object> obisValues = objectMapper.convertValue(
                        obisData.path(fk),
                        new TypeReference<List<Object>>() {}
                );

                // ✅ pass LocalDateTime
                DailyLoadProfileSinglePhase e = buildSPEntity(meterNo, mdasLdt, obisCodes, obisValues);
                enqueueSP(e);
                entities.add(e);

            } catch (Exception ex) {
                log.error("❌ DailyLP PG SP block failed for key {} => {}", fk, ex.getMessage(), ex);
            }
        }

        return entities.isEmpty() ? Flux.empty() : Flux.fromIterable(entities);
    }


    private Flux<DailyLoadProfileThreePhase> processThreePhase(
            String meterNo,
            Date mdasDateTime,
            List<String> obisCodes,
            List<String> fieldKeys,
            JsonNode obisData
    ) {
        List<DailyLoadProfileThreePhase> entities = new ArrayList<>();

        LocalDateTime mdasLdt = toLdt(mdasDateTime);

        for (String fk : fieldKeys) {
            if ("1".equals(fk)) continue;

            try {
                List<Object> obisValues = objectMapper.convertValue(
                        obisData.path(fk),
                        new TypeReference<List<Object>>() {}
                );

                DailyLoadProfileThreePhase e = buildTPEntity(meterNo, mdasLdt, obisCodes, obisValues);
                enqueueTP(e);
                entities.add(e);

            } catch (Exception ex) {
                log.error("❌ DailyLP PG TP block failed for key {} => {}", fk, ex.getMessage(), ex);
            }
        }

        return entities.isEmpty() ? Flux.empty() : Flux.fromIterable(entities);
    }

    // ---------------------------------------------------------
    // Enqueue helpers (drop-free)
    // ---------------------------------------------------------
    private void enqueueSP(DailyLoadProfileSinglePhase e) throws InterruptedException {
        spQueue.put(e);                 // BLOCKS → no data loss
        spQSize.incrementAndGet();       // ✅ FIX
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed("DailyData-1P", 1);
    }

    private void enqueueTP(DailyLoadProfileThreePhase e) throws InterruptedException {
        tpQueue.put(e);
        tpQSize.incrementAndGet();       // ✅ FIX
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed("DailyData-3P", 1);
    }

    // ---------------------------------------------------------
    // Drain & save (workers)
    // ---------------------------------------------------------
    
    private void drainAndSave() {
        try {
            // -------- SP drain ----------
            List<DailyLoadProfileSinglePhase> spBatch = new ArrayList<>(QUEUE_BATCH);
            int spDrained = spQueue.drainTo(spBatch, QUEUE_BATCH);

            if (spDrained > 0) {
                spQSize.addAndGet(-spDrained);

                // ✅ CRITICAL: queue se nikla => pending yahin minus
                pendingInserts.addAndGet(-spDrained);

                persistSP(spBatch).subscribe();
            }

            // -------- TP drain ----------
            List<DailyLoadProfileThreePhase> tpBatch = new ArrayList<>(QUEUE_BATCH);
            int tpDrained = tpQueue.drainTo(tpBatch, QUEUE_BATCH);

            if (tpDrained > 0) {
                tpQSize.addAndGet(-tpDrained);

                // ✅ CRITICAL
                pendingInserts.addAndGet(-tpDrained);

                persistTP(tpBatch).subscribe();
            }

            maybeResumeKafka();

        } catch (Exception e) {
            log.error("❌ DailyLP drain loop error", e);
        }
    }
    
    private Mono<Integer> persistSP(List<DailyLoadProfileSinglePhase> batch) {
        final int attempted = batch.size();

        return Mono.fromCallable(() -> {
                    try {
                        // ✅ FAST PATH: single TX batch
                        tx.execute(status -> {
                            spRepo.saveAll(batch);
                            return null;
                        });
                        return attempted; // processed
                    } catch (DataIntegrityViolationException dupOrConstraint) {
                        // ✅ fallback: per-row ignore duplicates
                        int ok = 0;
                        for (DailyLoadProfileSinglePhase e : batch) {
                            try {
                                spRepo.save(e);
                                ok++;
                            } catch (DataIntegrityViolationException ignoreDup) {
                                // duplicate => treat as processed
                                ok++;
                            }
                        }
                        return ok;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(processed -> {
                    // ✅ Dashboard should move (processed includes duplicates)
                    commandMetrics.incrementSaved("DailyData-1P", processed);
                })
                .onErrorResume(err -> {
                    log.error("❌ SP persist error → requeue {}", attempted, err);

                    // ✅ error means we removed from pending already, so add pending back
                    pendingInserts.addAndGet(attempted);

                    for (DailyLoadProfileSinglePhase e : batch) {
                        try {
                            spQueue.put(e);
                            spQSize.incrementAndGet();
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                    forcePause();
                    return Mono.just(0);
                });
    }
    private Mono<Integer> persistTP(List<DailyLoadProfileThreePhase> batch) {
        final int attempted = batch.size();

        return Mono.fromCallable(() -> {
                    try {
                        tx.execute(status -> {
                            tpRepo.saveAll(batch);
                            return null;
                        });
                        return attempted;
                    } catch (DataIntegrityViolationException dupOrConstraint) {
                        int ok = 0;
                        for (DailyLoadProfileThreePhase e : batch) {
                            try {
                                tpRepo.save(e);
                                ok++;
                            } catch (DataIntegrityViolationException ignoreDup) {
                                ok++;
                            }
                        }
                        return ok;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(processed -> {
                    commandMetrics.incrementSaved("DailyData-3P", processed);
                })
                .onErrorResume(err -> {
                    log.error("❌ TP persist error → requeue {}", attempted, err);

                    pendingInserts.addAndGet(attempted);

                    for (DailyLoadProfileThreePhase e : batch) {
                        try {
                            tpQueue.put(e);
                            tpQSize.incrementAndGet();
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                    forcePause();
                    return Mono.just(0);
                });
    }
    
    private void maybeResumeKafka() {
        if (!paused.get()) return;

        int size = spQueue.size() + tpQueue.size();
        int resumeThreshold = MAX_QUEUE_SIZE / 5;

        if (size <= resumeThreshold && paused.compareAndSet(true, false)) {
            MessageListenerContainer c =
                registry.getListenerContainer("DailyLoadProfile");

            if (c != null) {
                log.info("▶ Resuming DailyLoadProfile (queue={})", size);
                c.resume();
            }
        }
    }

    // ---------------------------------------------------------
    // CENTRAL BACKLOG MONITOR (pause/resume)
    // ---------------------------------------------------------
    @Scheduled(fixedDelay = 1000)
    public void monitorAndThrottle() {
        MessageListenerContainer c =
                registry.getListenerContainer("DailyLoadProfile");
        if (c == null) return;

        int queueBacklog = spQSize.get() + tpQSize.get();
        int totalBacklog = queueBacklog + pendingInserts.get();

        if (totalBacklog > MAX_PENDING_INSERTS &&
            paused.compareAndSet(false, true)) {

            log.warn("⏸ Pausing DailyLoadProfile (backlog={})", totalBacklog);
            c.pause();
            return;
        }

        if (totalBacklog < RESUME_THRESHOLD &&
            paused.compareAndSet(true, false)) {

            log.info("▶ Resuming DailyLoadProfile (backlog={})", totalBacklog);
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c = registry.getListenerContainer("DailyLoadProfile");
        if (c != null && paused.compareAndSet(false, true)) {
            log.warn("⏸ Force-pausing DailyLoadProfile listener: PG unavailable / backlog high");
            c.pause();
        }
    }

    // ---------------------------------------------------------
    // Entity builders (IMPORTANT: day = date-only of datetime)
    // ---------------------------------------------------------
    private DailyLoadProfileSinglePhase buildSPEntity(
            String meterNo,
            LocalDateTime mdasDateTime,
            List<String> obisCodes,
            List<Object> obisValues
    ) {
        DailyLoadProfileSinglePhase e = new DailyLoadProfileSinglePhase();

        // ✅ 1) datetime ONLY from OBIS (never from mapping)
        LocalDateTime dt = extractObisDatetime(obisCodes, obisValues, mdasDateTime);

        
        // ✅ 3) EmbeddedId (ONLY ONE place datetime is set)
        DailyLoadProfileSinglePhaseId id = new DailyLoadProfileSinglePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setDatetime(dt);
        e.setId(id);

        // ✅ 4) mdasDatetime = Kafka systemTime ONLY
        e.setMdasDatetime(mdasDateTime);
        e.setReadingType(1);

        // ✅ 5) Map OBIS values (datetime already handled → mapping must skip it)
        Map<String, String> mapping =
                ObisFieldMapping.getDailyLpFieldMappingSinglePhase();
        mapObisDataToEntity(e, obisCodes, obisValues, mapping);

        return e;
    }


    private DailyLoadProfileThreePhase buildTPEntity(
            String meterNo,
            LocalDateTime mdasDateTime,
            List<String> obisCodes,
            List<Object> obisValues
    ) {
        DailyLoadProfileThreePhase e = new DailyLoadProfileThreePhase();

        // ✅ 1) datetime ONLY from OBIS
        LocalDateTime dt = extractObisDatetime(obisCodes, obisValues, mdasDateTime);


        // ✅ 3) EmbeddedId
        DailyLoadProfileThreePhaseId id = new DailyLoadProfileThreePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setDatetime(dt);
        e.setId(id);

        // ✅ 4) mdasDatetime = Kafka systemTime
        e.setMdasDatetime(mdasDateTime);
        
        e.setReadingType(1);


        // ✅ 5) Map remaining OBIS values
        Map<String, String> mapping =
                ObisFieldMapping.getDailyLpFieldMappingThreePhase();
        mapObisDataToEntity(e, obisCodes, obisValues, mapping);

        return e;
    }




    // ---------------------------------------------------------
    // OBIS → Entity mapping (reflection, same concept as Cassandra)
    // ---------------------------------------------------------
    private void mapObisDataToEntity(
            Object entity,
            List<String> obisCodes,
            List<Object> obisValues,
            Map<String, String> mapping
    ) {
        if (obisCodes == null || obisValues == null) return;

        int size = Math.min(obisCodes.size(), obisValues.size());

        for (int i = 0; i < size; i++) {

            String obis = obisCodes.get(i);

            // ✅ CRITICAL FIX (3P WAS FAILING HERE)
            if (DT_OBIS.equals(obis)) {
                continue;
            }

            Object value = obisValues.get(i);
            String fieldName = mapping.get(obis);

            if (fieldName == null || value == null) continue;

            try {
                Field field = resolveField(entity.getClass(), fieldName);
                Class<?> t = field.getType();

                if (t == Double.class || t == double.class) {
                    field.set(entity, Double.parseDouble(value.toString()));
                } else if (t == Integer.class || t == int.class) {
                    field.set(entity, Integer.parseInt(value.toString()));
                } else if (t == Date.class) {
                    String cleaned = value.toString()
                            .replaceAll("[^0-9:\\- ]", " ")
                            .replaceAll("\\s+", " ")
                            .trim();
                    field.set(entity, DF.get().parse(cleaned));
                } else {
                    field.set(entity, value);
                }

            } catch (Exception e) {
                log.error("❌ PG Mapping failed for OBIS {} => {}", obis, e.getMessage());
            }
        }
    }


    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------
    private static Date parseDateOrNow(String str) {
        try {
            if (str == null || str.isBlank()) return new Date();
            String cleaned = str.replaceAll("[^0-9:\\- ]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
            return DF.get().parse(cleaned);
        } catch (Exception e) {
            return new Date();
        }
    }
    
    private static Field resolveField(Class<?> clazz, String name) throws NoSuchFieldException {

        // 1) exact match
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException ignore) {}

        // 2) snake_case -> camelCase
        String camel = snakeToCamel(name);
        try {
            Field f = clazz.getDeclaredField(camel);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException ignore) {}

        // 3) case-insensitive fallback (scans all fields)
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equalsIgnoreCase(name) || f.getName().equalsIgnoreCase(camel)) {
                f.setAccessible(true);
                return f;
            }
        }

        throw new NoSuchFieldException(name);
    }

    private static String snakeToCamel(String s) {
        if (s == null || s.isBlank() || !s.contains("_")) return s;
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char c : s.toCharArray()) {
            if (c == '_') { up = true; continue; }
            sb.append(up ? Character.toUpperCase(c) : c);
            up = false;
        }
        return sb.toString();
    }
    
    

    

    private static LocalDateTime parseToLdt(String str) {
        if (str == null || str.isBlank()) return LocalDateTime.now();

        String s = str.trim();

        try {
            // 1) ISO like 2025-01-27T19:54:50Z or 2025-01-27T19:54:50+00:00
            if (s.contains("T")) {
                return LocalDateTime.ofInstant(java.time.Instant.parse(s), ZoneId.systemDefault());
            }
        } catch (Exception ignore) {}

        try {
            // 2) clean to "yyyy-MM-dd HH:mm:ss" (remove millis)
            s = s.replace("T", " ").replace("Z", "");
            s = s.replaceAll("\\.\\d+", ""); // remove .000 etc
            s = s.replaceAll("[^0-9:\\- ]", " ").replaceAll("\\s+", " ").trim();

            Date d = DF.get().parse(s);
            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    private static LocalDateTime extractObisDatetime(
            List<String> obisCodes,
            List<Object> obisValues,
            LocalDateTime fallback
    ) {
        try {
            if (obisCodes != null && obisValues != null) {
                int n = Math.min(obisCodes.size(), obisValues.size());
                for (int i = 0; i < n; i++) {
                    if (DT_OBIS.equals(obisCodes.get(i)) && obisValues.get(i) != null) {
                        return parseToLdt(obisValues.get(i).toString());
                    }
                }
            }
        } catch (Exception e) {
            // ignore, fallback below
        }
        return fallback != null ? fallback : LocalDateTime.now();
    }

 
    
    private static LocalDateTime toLdt(Date d) {
        if (d == null) return LocalDateTime.now();
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }



}

