package com.jne.utils;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.LoadProfileSinglePhase;
import com.jne.model.LoadProfileSinglePhaseId;
import com.jne.model.LoadProfileThreePhase;
import com.jne.model.LoadProfileThreePhaseId;
import com.jne.repo.DeltaLoadProfileSinglePhaseRepo;
import com.jne.repo.DeltaLoadProfileThreePhaseRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class DeltaLoadProfileReadConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeltaLoadProfileReadConsumer.class);

    // ---------------- CONFIG ----------------
    private static final int MAX_QUEUE_SIZE   = 25000;
    private static final int RESUME_THRESHOLD = 10000;
    private static final int QUEUE_BATCH      = 5000;

    private static final int WRITE_CONCURRENCY = 4;
    private static final int BUFFER_SIZE       = 2000;

    // ---------------- QUEUES ----------------
    private final BlockingQueue<LoadProfileSinglePhase> spQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final BlockingQueue<LoadProfileThreePhase> tpQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final AtomicBoolean paused = new AtomicBoolean(false);
    
    private final BlockingQueue<Acknowledgment> ackQueue = new LinkedBlockingQueue<>();

    // DB concurrency control
    private final Semaphore dbPermits = new Semaphore(4);
    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(4);
    
    @PersistenceContext
    private EntityManager em;

    // ---------------- DEPENDENCIES ----------------
    private final DeltaLoadProfileSinglePhaseRepo spRepo;
    private final DeltaLoadProfileThreePhaseRepo tpRepo;
    private final KafkaListenerEndpointRegistry registry;
    private final TransactionTemplate tx;
    private final KafkaCommandMetrics commandMetrics;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduledExecutorService drainScheduler =
            Executors.newSingleThreadScheduledExecutor();
    

    private static final ThreadLocal<SimpleDateFormat> DF =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setLenient(false);
                return sdf;
            });

    public DeltaLoadProfileReadConsumer(
            DeltaLoadProfileSinglePhaseRepo spRepo,
            DeltaLoadProfileThreePhaseRepo tpRepo,
            KafkaListenerEndpointRegistry registry,
            PlatformTransactionManager txManager,
            KafkaCommandMetrics commandMetrics
    ) {
        this.spRepo = spRepo;
        this.tpRepo = tpRepo;
        this.registry = registry;
        this.tx = new TransactionTemplate(txManager);
        this.commandMetrics = commandMetrics;
    }

    // =========================================================
    // KAFKA CONSUMER
    // =========================================================
    @KafkaListener(
            id = "DeltaLoadProfile",
            topics = "DeltaLoadProfile",
            groupId = "${kafka.dlp.group-id}",
            concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {

        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        try {

            for (String msg : messages) {

                JsonNode root = objectMapper.readTree(msg);

                processMessage(root);
            }

            // store ack AFTER processing
            ackQueue.offer(ack);

        } catch (Exception e) {

            log.error("❌ Consume error", e);

            forcePause();
        }
    }

    private void processMessage(JsonNode root) {

        String meterType  = root.path("meterType").asText(null);
        String meterNo    = root.path("meterNo").asText(null);
        String systemTime = root.path("systemTime").asText(null);

        LocalDateTime mdasLdt = parseToLdt(systemTime);

        JsonNode dataNode = root.path("data");

        if (!(dataNode.isArray() && dataNode.size() > 0)) {
            return;
        }

        JsonNode obisData = dataNode.get(0);

        List<String> obisCodes = objectMapper.convertValue(
                obisData.path("1"),
                new TypeReference<List<String>>() {}
        );

        List<String> fieldKeys = new ArrayList<>();

        obisData.fieldNames().forEachRemaining(fk -> {

            if (!"1".equals(fk)) {
                fieldKeys.add(fk);
            }
        });

        if ("Single Phase".equalsIgnoreCase(meterType)) {

            processSinglePhase(
                    meterNo,
                    mdasLdt,
                    obisCodes,
                    fieldKeys,
                    obisData
            );

        } else {

            processThreePhase(
                    meterNo,
                    mdasLdt,
                    obisCodes,
                    fieldKeys,
                    obisData
            );
        }
    }

    // =========================================================
    // BUILD + ENQUEUE
    // =========================================================
    private void processSinglePhase(
            String meterNo,
            LocalDateTime mdasLdt,
            List<String> obisCodes,
            List<String> fieldKeys,
            JsonNode obisData) {

        for (String fk : fieldKeys) {

            try {

                List<Object> vals = objectMapper.convertValue(
                        obisData.path(fk),
                        new TypeReference<List<Object>>() {}
                );

                LoadProfileSinglePhase e =
                        buildSinglePhase(meterNo, mdasLdt, obisCodes, vals);

                enqueueSP(e);

            } catch (Exception ex) {

                log.error("❌ SP error {}", ex.toString());
            }
        }
    }

    private void processThreePhase(
            String meterNo,
            LocalDateTime mdasLdt,
            List<String> obisCodes,
            List<String> fieldKeys,
            JsonNode obisData) {

        for (String fk : fieldKeys) {

            try {

                List<Object> vals = objectMapper.convertValue(
                        obisData.path(fk),
                        new TypeReference<List<Object>>() {}
                );

                LoadProfileThreePhase e =
                        buildThreePhase(meterNo, mdasLdt, obisCodes, vals);

                enqueueTP(e);

            } catch (Exception ex) {

                log.error("❌ TP error {}", ex.toString());
            }
        }
    }

    // =========================================================
    // ENQUEUE (SAFE)
    // =========================================================
    private void enqueueSP(LoadProfileSinglePhase e) {

        while (!spQueue.offer(e)) {

            log.warn("⚠ SP queue full. Pausing Kafka...");

            forcePause();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        commandMetrics.incrementConsumed("LoadProfile-PG-1P", 1);
    }

    private void enqueueTP(LoadProfileThreePhase e) {

        while (!tpQueue.offer(e)) {

            log.warn("⚠ TP queue full. Pausing Kafka...");

            forcePause();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        commandMetrics.incrementConsumed("LoadProfile-PG-3P", 1);
    }

    // =========================================================
    // DRAIN LOOP (ASYNC DB)
    // =========================================================
    @PostConstruct
    public void startDrainLoop() {
        drainScheduler.scheduleWithFixedDelay(
                this::drainAndSave,
                0,
                200,
                TimeUnit.MILLISECONDS
        );
    }

    private void drainAndSave() {

        try {

            List<LoadProfileSinglePhase> spBatch =
                    new ArrayList<>(QUEUE_BATCH);

            spQueue.drainTo(spBatch, QUEUE_BATCH);

            List<LoadProfileThreePhase> tpBatch =
                    new ArrayList<>(QUEUE_BATCH);

            tpQueue.drainTo(tpBatch, QUEUE_BATCH);

            if (spBatch.isEmpty() && tpBatch.isEmpty()) {
                return;
            }

            CompletableFuture<Void> spFuture =
                    spBatch.isEmpty()
                            ? CompletableFuture.completedFuture(null)
                            : CompletableFuture.runAsync(
                                    () -> persistSP(spBatch),
                                    dbExecutor
                            );

            CompletableFuture<Void> tpFuture =
                    tpBatch.isEmpty()
                            ? CompletableFuture.completedFuture(null)
                            : CompletableFuture.runAsync(
                                    () -> persistTP(tpBatch),
                                    dbExecutor
                            );

            CompletableFuture.allOf(spFuture, tpFuture).join();

            // ACK ONLY AFTER SUCCESS
            Acknowledgment ack;

            while ((ack = ackQueue.poll()) != null) {

                try {

                    ack.acknowledge();

                } catch (Exception ex) {

                    log.error("❌ Ack error", ex);
                }
            }

            maybeResumeKafka();

        } catch (Exception e) {

            log.error("❌ drain failed", e);

            forcePause();
        }
    }

    // =========================================================
    // DB SAVE (NON-BLOCKING SAFE)
    // =========================================================
   

    private void persistSP(List<LoadProfileSinglePhase> batch) {

        try {

            dbPermits.acquire();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return;
        }

        try {

            tx.execute(status -> {

                int count = 0;

                for (LoadProfileSinglePhase e : batch) {

                    try {

                        spRepo.insertIgnore(

                            e.getId().getDeviceSerialNumber(),
                            e.getId().getIntervalDatetime(),

                            e.getAverageVoltage(),
                            e.getAverageCurrent(),

                            e.getBlockEnergyKwhImport(),
                            e.getBlockEnergyKwhExport(),

                            e.getBlockEnergyKvahImport(),
                            e.getBlockEnergyKvahExport(),

                            e.getMdasDatetime(),
                            e.getMeterDatetime()
                        );

                        count++;

                        // IMPORTANT
                        
                        if (count % 100 == 0) {

                            em.flush();
                            em.clear();
                        }

                    } catch (Exception ex) {

                        log.error("❌ SP Insert error: {}", ex.getMessage());
                    }
                }

                em.flush();
                em.clear();

                return null;
            });

            commandMetrics.incrementSaved("LoadProfile-PG-1P", batch.size());

        } catch (Exception e) {

            log.error("❌ SP DB error", e);

        } finally {

            dbPermits.release();
        }
    }

    private void persistTP(List<LoadProfileThreePhase> batch) {

        try {

            dbPermits.acquire();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return;
        }

        try {

            tx.execute(status -> {

                int count = 0;

                for (LoadProfileThreePhase e : batch) {

                    try {

                        tpRepo.insertIgnore(

                            e.getId().getDeviceSerialNumber(),
                            e.getId().getIntervalDatetime(),

                            e.getPhaseCurrentL1(),
                            e.getPhaseCurrentL2(),
                            e.getPhaseCurrentL3(),

                            e.getPhaseVoltageL1(),
                            e.getPhaseVoltageL2(),
                            e.getPhaseVoltageL3(),

                            e.getBlockEnergyKwhImport(),
                            e.getBlockEnergyKwhExport(),

                            e.getBlockEnergyKvahImport(),
                            e.getBlockEnergyKvahExport(),

                            e.getMdasDatetime()
                        );

                        count++;

                        if (count % 100 == 0) {

                            em.flush();
                            em.clear();
                        }

                    } catch (Exception ex) {

                        log.error("❌ TP Insert error: {}", ex.getMessage());
                    }
                }

                em.flush();
                em.clear();

                return null;
            });

            commandMetrics.incrementSaved("LoadProfile-PG-3P", batch.size());

        } catch (Exception e) {

            log.error("❌ TP DB error", e);

        } finally {

            dbPermits.release();
        }
    }

    // =========================================================
    // PAUSE / RESUME
    // =========================================================
    private void maybeResumeKafka() {

        if (!paused.get()) return;

        int size = spQueue.size() + tpQueue.size();

        if (size < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
            MessageListenerContainer c =
                    registry.getListenerContainer("DeltaLoadProfile");

            if (c != null) {
                log.info("▶ Resuming Kafka (queue={})", size);
                c.resume();
            }
        }
    }

    private void forcePause() {
        MessageListenerContainer c =
                registry.getListenerContainer("DeltaLoadProfile");

        if (c != null && paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing Kafka (queue full)");
            c.pause();
        }
    }

    // =========================================================
    // ENTITY BUILDERS
    // =========================================================
    private LoadProfileSinglePhase buildSinglePhase(
            String meterNo,
            LocalDateTime mdasLdt,
            List<String> obisCodes,
            List<Object> obisValues) {

        LoadProfileSinglePhase e = new LoadProfileSinglePhase();
        LoadProfileSinglePhaseId id = new LoadProfileSinglePhaseId();

        id.setDeviceSerialNumber(meterNo);
        LocalDateTime interval = null;

        for (int i = 0; i < obisCodes.size(); i++) {

            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);

            String field = ObisFieldMapping.getDeltaFieldMappingSinglePhase().get(obis);
            if (field == null) continue;

            switch (field) {

                case "intervalDatetime":
                    interval = parseToLdt(String.valueOf(value));
                    if (interval != null) {
                        id.setIntervalDatetime(interval);
                        e.setMeterDatetime(interval);
                    }
                    break;

                case "averageVoltage":
                    e.setAverageVoltage(parseDouble(value));
                    break;

                case "averageCurrent":
                    e.setAverageCurrent(parseDouble(value));
                    break;

                case "blockEnergyKwhImport":
                    e.setBlockEnergyKwhImport(parseDouble(value));
                    break;

                case "blockEnergyKwhExport":
                    e.setBlockEnergyKwhExport(parseDouble(value));
                    break;

                case "blockEnergyKvahImport":
                    e.setBlockEnergyKvahImport(parseDouble(value));
                    break;

                case "blockEnergyKvahExport":
                    e.setBlockEnergyKvahExport(parseDouble(value));
                    break;
            }
        }

        // fallback (important)
        if (interval == null) {
            interval = mdasLdt != null ? mdasLdt : LocalDateTime.now();
            id.setIntervalDatetime(interval);
            e.setMeterDatetime(interval);
        }

        e.setId(id);
        e.setMdasDatetime(mdasLdt);
        e.setReadingType(1);


        return e;
    }

    private LoadProfileThreePhase buildThreePhase(
            String meterNo,
            LocalDateTime mdasLdt,
            List<String> obisCodes,
            List<Object> obisValues) {

        LoadProfileThreePhase e = new LoadProfileThreePhase();
        LoadProfileThreePhaseId id = new LoadProfileThreePhaseId();

        id.setDeviceSerialNumber(meterNo);
        LocalDateTime interval = null;

        for (int i = 0; i < obisCodes.size(); i++) {

            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);

            String field = ObisFieldMapping.getDeltaFieldMappingThreePhase().get(obis);
            if (field == null) continue;

            switch (field) {

                case "intervalDatetime":
                    interval = parseToLdt(String.valueOf(value));
                    if (interval != null) {
                        id.setIntervalDatetime(interval);
                    }
                    break;

                case "phaseCurrentL1":
                    e.setPhaseCurrentL1(parseDouble(value));
                    break;

                case "phaseCurrentL2":
                    e.setPhaseCurrentL2(parseDouble(value));
                    break;

                case "phaseCurrentL3":
                    e.setPhaseCurrentL3(parseDouble(value));
                    break;

                case "phaseVoltageL1":
                    e.setPhaseVoltageL1(parseDouble(value));
                    break;

                case "phaseVoltageL2":
                    e.setPhaseVoltageL2(parseDouble(value));
                    break;

                case "phaseVoltageL3":
                    e.setPhaseVoltageL3(parseDouble(value));
                    break;

                case "blockEnergyKwhImport":
                    e.setBlockEnergyKwhImport(parseDouble(value));
                    break;

                case "blockEnergyKwhExport":
                    e.setBlockEnergyKwhExport(parseDouble(value));
                    break;

                case "blockEnergyKvahImport":
                    e.setBlockEnergyKvahImport(parseDouble(value));
                    break;

                case "blockEnergyKvahExport":
                    e.setBlockEnergyKvahExport(parseDouble(value));
                    break;
            }
        }

        // fallback (important)
        if (interval == null) {
            interval = mdasLdt != null ? mdasLdt : LocalDateTime.now();
            id.setIntervalDatetime(interval);
        }

        e.setId(id);
        e.setMdasDatetime(mdasLdt);
        e.setReadingType(1);


        return e;
    }
    
    private Double parseDouble(Object val) {
        try {
            return val != null ? Double.valueOf(val.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static LocalDateTime parseToLdt(String str) {

        if (str == null || str.trim().isEmpty()) {
            return null;
        }

        str = str.trim();

        // =====================================================
        // UTC ISO FORMAT
        // Example:
        // 2026-05-27T18:30:00Z
        // 2026-05-27T18:30:00.000Z
        // =====================================================

        try {

            return OffsetDateTime
                    .parse(str)
                    .toLocalDateTime();

        } catch (Exception ignored) {}

        // =====================================================
        // yyyy-MM-dd HH:mm:ss
        // =====================================================

        try {

            return LocalDateTime.parse(
                str,
                java.time.format.DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
                )
            );

        } catch (Exception ignored) {}

        // =====================================================
        // yyyy-MM-dd'T'HH:mm:ss
        // =====================================================

        try {

            return LocalDateTime.parse(
                str,
                java.time.format.DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm:ss"
                )
            );

        } catch (Exception ignored) {}

        // =====================================================
        // FAIL
        // =====================================================

        log.error("❌ Unable to parse datetime: {}", str);

        return null;
    }
}




//package com.jne.utils;
//
//
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.kafka.listener.MessageListenerContainer;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jne.model.LoadProfileSinglePhase;
//import com.jne.model.LoadProfileSinglePhaseId;
//import com.jne.model.LoadProfileThreePhase;
//import com.jne.model.LoadProfileThreePhaseId;
//import com.jne.repo.DeltaLoadProfileSinglePhaseRepo;
//import com.jne.repo.DeltaLoadProfileThreePhaseRepo;
//import com.jne.service.utils.KafkaCommandMetrics;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Component
//@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
//@EnableScheduling
//public class DeltaLoadProfileReadConsumer {
//
//    private static final Logger log = LoggerFactory.getLogger(DeltaLoadProfileReadConsumer.class);
//
//    // ---------------- CONFIG ----------------
//    private static final int MAX_QUEUE_SIZE   = 25000;
//    private static final int RESUME_THRESHOLD = 10000;
//    private static final int QUEUE_BATCH      = 5000;
//
//    private static final int WRITE_CONCURRENCY = 8;
//    private static final int BUFFER_SIZE       = 2000;
//
//    // ---------------- QUEUES ----------------
//    private final BlockingQueue<LoadProfileSinglePhase> spQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//
//    private final BlockingQueue<LoadProfileThreePhase> tpQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//
//    private final AtomicBoolean paused = new AtomicBoolean(false);
//    
//    private final BlockingQueue<Acknowledgment> ackQueue = new LinkedBlockingQueue<>();
//
//    // DB concurrency control
//    private final Semaphore dbPermits = new Semaphore(4);
//    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(4);
//    
//    @PersistenceContext
//    private EntityManager em;
//
//    // ---------------- DEPENDENCIES ----------------
//    private final DeltaLoadProfileSinglePhaseRepo spRepo;
//    private final DeltaLoadProfileThreePhaseRepo tpRepo;
//    private final KafkaListenerEndpointRegistry registry;
//    private final TransactionTemplate tx;
//    private final KafkaCommandMetrics commandMetrics;
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    private final ScheduledExecutorService drainScheduler =
//            Executors.newScheduledThreadPool(4);
//    
//
//    private static final ThreadLocal<SimpleDateFormat> DF =
//            ThreadLocal.withInitial(() -> {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                sdf.setLenient(false);
//                return sdf;
//            });
//
//    public DeltaLoadProfileReadConsumer(
//            DeltaLoadProfileSinglePhaseRepo spRepo,
//            DeltaLoadProfileThreePhaseRepo tpRepo,
//            KafkaListenerEndpointRegistry registry,
//            PlatformTransactionManager txManager,
//            KafkaCommandMetrics commandMetrics
//    ) {
//        this.spRepo = spRepo;
//        this.tpRepo = tpRepo;
//        this.registry = registry;
//        this.tx = new TransactionTemplate(txManager);
//        this.commandMetrics = commandMetrics;
//    }
//
//    // =========================================================
//    // KAFKA CONSUMER
//    // =========================================================
//    @KafkaListener(
//            id = "DeltaLoadProfile",
//            topics = "DeltaLoadProfile",
//            groupId = "${kafka.dlp.group-id}",
//            concurrency = "${feature.queue.concurrency:6}"
//    )
//    public void consume(List<String> messages, Acknowledgment ack) {
//
//        if (messages == null || messages.isEmpty()) {
//            ack.acknowledge();
//            return;
//        }
//
//        Flux.fromIterable(messages)
//            .flatMap(msg -> Mono.fromCallable(() -> objectMapper.readTree(msg)), WRITE_CONCURRENCY)
//            .flatMap(this::processMessage, WRITE_CONCURRENCY)
//            .then()
//            .doOnSuccess(v -> {
//                ackQueue.offer(ack);   // ✅ only store
//            })
//            .doOnError(err -> {
//                log.error("❌ Pipeline error", err);
//                forcePause();
//            })
//            .subscribe();
//    }
//
//    private Flux<Void> processMessage(JsonNode root) {
//
//        String meterType  = root.path("meterType").asText(null);
//        String meterNo    = root.path("meterNo").asText(null);
//        String systemTime = root.path("systemTime").asText(null);
//
//        LocalDateTime mdasLdt = parseToLdt(systemTime);
//        if (mdasLdt == null) mdasLdt = LocalDateTime.now();
//
//        JsonNode dataNode = root.path("data");
//        if (!(dataNode.isArray() && dataNode.size() > 0)) return Flux.empty();
//
//        JsonNode obisData = dataNode.get(0);
//
//        List<String> obisCodes = objectMapper.convertValue(
//                obisData.path("1"),
//                new TypeReference<List<String>>() {}
//        );
//
//        List<String> fieldKeys = new ArrayList<>();
//        obisData.fieldNames().forEachRemaining(fk -> {
//            if (!"1".equals(fk)) fieldKeys.add(fk);
//        });
//
//        if ("Single Phase".equalsIgnoreCase(meterType)) {
//            return processSinglePhase(meterNo, mdasLdt, obisCodes, fieldKeys, obisData);
//        } else {
//            return processThreePhase(meterNo, mdasLdt, obisCodes, fieldKeys, obisData);
//        }
//    }
//
//    // =========================================================
//    // BUILD + ENQUEUE
//    // =========================================================
//    private Flux<Void> processSinglePhase(
//            String meterNo,
//            LocalDateTime mdasLdt,
//            List<String> obisCodes,
//            List<String> fieldKeys,
//            JsonNode obisData) {
//
//        for (String fk : fieldKeys) {
//            try {
//                List<Object> vals = objectMapper.convertValue(
//                        obisData.path(fk),
//                        new TypeReference<List<Object>>() {}
//                );
//
//                LoadProfileSinglePhase e =
//                        buildSinglePhase(meterNo, mdasLdt, obisCodes, vals);
//
//                enqueueSP(e);
//
//            } catch (Exception ex) {
//                log.error("❌ SP error {}", ex.toString());
//            }
//        }
//        return Flux.empty();
//    }
//
//    private Flux<Void> processThreePhase(
//            String meterNo,
//            LocalDateTime mdasLdt,
//            List<String> obisCodes,
//            List<String> fieldKeys,
//            JsonNode obisData) {
//
//        for (String fk : fieldKeys) {
//            try {
//                List<Object> vals = objectMapper.convertValue(
//                        obisData.path(fk),
//                        new TypeReference<List<Object>>() {}
//                );
//
//                LoadProfileThreePhase e =
//                        buildThreePhase(meterNo, mdasLdt, obisCodes, vals);
//
//                enqueueTP(e);
//
//            } catch (Exception ex) {
//                log.error("❌ TP error {}", ex.toString());
//            }
//        }
//        return Flux.empty();
//    }
//
//    // =========================================================
//    // ENQUEUE (SAFE)
//    // =========================================================
//    private void enqueueSP(LoadProfileSinglePhase e) {
//        if (spQueue.size() + tpQueue.size() >= MAX_QUEUE_SIZE) {
//            forcePause();
//            return;
//        }
//
//        if (spQueue.offer(e)) {
//            commandMetrics.incrementConsumed("LoadProfile-PG-1P", 1);
//        }
//    }
//
//    private void enqueueTP(LoadProfileThreePhase e) {
//        if (spQueue.size() + tpQueue.size() >= MAX_QUEUE_SIZE) {
//            forcePause();
//            return;
//        }
//
//        if (tpQueue.offer(e)) {
//            commandMetrics.incrementConsumed("LoadProfile-PG-3P", 1);
//        }
//    }
//
//    // =========================================================
//    // DRAIN LOOP (ASYNC DB)
//    // =========================================================
//    @PostConstruct
//    public void startDrainLoop() {
//        drainScheduler.scheduleWithFixedDelay(
//                this::drainAndSave,
//                0,
//                200,
//                TimeUnit.MILLISECONDS
//        );
//    }
//
//    private void drainAndSave() {
//
//        try {
//            List<LoadProfileSinglePhase> spBatch = new ArrayList<>(QUEUE_BATCH);
//            spQueue.drainTo(spBatch, QUEUE_BATCH);
//
//            List<LoadProfileThreePhase> tpBatch = new ArrayList<>(QUEUE_BATCH);
//            tpQueue.drainTo(tpBatch, QUEUE_BATCH);
//
//            if (spBatch.isEmpty() && tpBatch.isEmpty()) return;
//
//            CompletableFuture<Void> spFuture = spBatch.isEmpty()
//                    ? CompletableFuture.completedFuture(null)
//                    : CompletableFuture.runAsync(() -> persistSP(spBatch), dbExecutor);
//
//            CompletableFuture<Void> tpFuture = tpBatch.isEmpty()
//                    ? CompletableFuture.completedFuture(null)
//                    : CompletableFuture.runAsync(() -> persistTP(tpBatch), dbExecutor);
//
//            CompletableFuture.allOf(spFuture, tpFuture)
//                .thenRun(() -> {
//                    // ✅ ACK AFTER BOTH DB SUCCESS
//                    Acknowledgment ack;
//                    while ((ack = ackQueue.poll()) != null) {
//                        ack.acknowledge();
//                    }
//                });
//
//            maybeResumeKafka();
//
//        } catch (Exception e) {
//            log.error("❌ drain failed", e);
//        }
//    }
//
//    // =========================================================
//    // DB SAVE (NON-BLOCKING SAFE)
//    // =========================================================
//   
//
//    private void persistSP(List<LoadProfileSinglePhase> batch) {
//
//        try {
//            dbPermits.acquire();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return;
//        }
//
//        try {
//            tx.execute(status -> {
//
//                for (LoadProfileSinglePhase e : batch) {
//                    try {
//                        spRepo.insertIgnore(
//                            e.getId().getDeviceSerialNumber(),
//                            e.getId().getIntervalDatetime(),
//                            e.getMdasDatetime(),
//                            e.getMeterDatetime()
//                        );
//                    } catch (Exception ex) {
//                        log.error("❌ Insert error: {}", ex.getMessage());
//                    }
//                }
//
//                return null;
//            });
//
//            commandMetrics.incrementSaved("LoadProfile-PG-1P", batch.size());
//
//        } catch (Exception e) {
//            log.error("❌ SP DB error", e);
//        } finally {
//            dbPermits.release();
//        }
//    }
//
//    private void persistTP(List<LoadProfileThreePhase> batch) {
//
//        try {
//            dbPermits.acquire();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return;
//        }
//
//        try {
//            tx.execute(status -> {
//
//                for (LoadProfileThreePhase e : batch) {
//                    try {
//                        tpRepo.insertIgnore(
//                            e.getId().getDeviceSerialNumber(),
//                            e.getId().getIntervalDatetime(),
//                            e.getMdasDatetime()
//                        );
//                    } catch (Exception ex) {
//                        log.error("❌ TP Insert error: {}", ex.getMessage());
//                    }
//                }
//
//                return null;
//            });
//
//            commandMetrics.incrementSaved("LoadProfile-PG-3P", batch.size());
//
//        } catch (Exception e) {
//            log.error("❌ TP DB error", e);
//        } finally {
//            dbPermits.release();
//        }
//    }
//
//    // =========================================================
//    // PAUSE / RESUME
//    // =========================================================
//    private void maybeResumeKafka() {
//
//        if (!paused.get()) return;
//
//        int size = spQueue.size() + tpQueue.size();
//
//        if (size < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
//            MessageListenerContainer c =
//                    registry.getListenerContainer("DeltaLoadProfile");
//
//            if (c != null) {
//                log.info("▶ Resuming Kafka (queue={})", size);
//                c.resume();
//            }
//        }
//    }
//
//    private void forcePause() {
//        MessageListenerContainer c =
//                registry.getListenerContainer("DeltaLoadProfile");
//
//        if (c != null && paused.compareAndSet(false, true)) {
//            log.warn("⏸ Pausing Kafka (queue full)");
//            c.pause();
//        }
//    }
//
//    // =========================================================
//    // ENTITY BUILDERS
//    // =========================================================
//    private LoadProfileSinglePhase buildSinglePhase(
//            String meterNo,
//            LocalDateTime mdasLdt,
//            List<String> obisCodes,
//            List<Object> obisValues) {
//
//        LoadProfileSinglePhase e = new LoadProfileSinglePhase();
//        LoadProfileSinglePhaseId id = new LoadProfileSinglePhaseId();
//
//        id.setDeviceSerialNumber(meterNo);
//        LocalDateTime interval = null;
//
//        for (int i = 0; i < obisCodes.size(); i++) {
//
//            String obis = obisCodes.get(i);
//            Object value = obisValues.get(i);
//
//            String field = ObisFieldMapping.getDeltaFieldMappingSinglePhase().get(obis);
//            if (field == null) continue;
//
//            switch (field) {
//
//                case "meterDatetime":
//                    interval = parseToLdt(String.valueOf(value));
//                    if (interval != null) {
//                        id.setIntervalDatetime(interval);
//                        e.setMeterDatetime(interval);
//                    }
//                    break;
//
//                case "averageVoltage":
//                    e.setAverageVoltage(parseDouble(value));
//                    break;
//
//                case "averageCurrent":
//                    e.setAverageCurrent(parseDouble(value));
//                    break;
//
//                case "blockEnergyKwhImport":
//                    e.setBlockEnergyKwhImport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKwhExport":
//                    e.setBlockEnergyKwhExport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKvahImport":
//                    e.setBlockEnergyKvahImport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKvahExport":
//                    e.setBlockEnergyKvahExport(parseDouble(value));
//                    break;
//            }
//        }
//
//        // fallback (important)
//        if (interval == null) {
//            interval = mdasLdt != null ? mdasLdt : LocalDateTime.now();
//            id.setIntervalDatetime(interval);
//            e.setMeterDatetime(interval);
//        }
//
//        e.setId(id);
//        e.setMdasDatetime(mdasLdt);
//
//        return e;
//    }
//
//    private LoadProfileThreePhase buildThreePhase(
//            String meterNo,
//            LocalDateTime mdasLdt,
//            List<String> obisCodes,
//            List<Object> obisValues) {
//
//        LoadProfileThreePhase e = new LoadProfileThreePhase();
//        LoadProfileThreePhaseId id = new LoadProfileThreePhaseId();
//
//        id.setDeviceSerialNumber(meterNo);
//        LocalDateTime interval = null;
//
//        for (int i = 0; i < obisCodes.size(); i++) {
//
//            String obis = obisCodes.get(i);
//            Object value = obisValues.get(i);
//
//            String field = ObisFieldMapping.getDeltaFieldMappingThreePhase().get(obis);
//            if (field == null) continue;
//
//            switch (field) {
//
//                case "intervalDatetime":
//                    interval = parseToLdt(String.valueOf(value));
//                    if (interval != null) {
//                        id.setIntervalDatetime(interval);
//                    }
//                    break;
//
//                case "phaseCurrentL1":
//                    e.setPhaseCurrentL1(parseDouble(value));
//                    break;
//
//                case "phaseCurrentL2":
//                    e.setPhaseCurrentL2(parseDouble(value));
//                    break;
//
//                case "phaseCurrentL3":
//                    e.setPhaseCurrentL3(parseDouble(value));
//                    break;
//
//                case "phaseVoltageL1":
//                    e.setPhaseVoltageL1(parseDouble(value));
//                    break;
//
//                case "phaseVoltageL2":
//                    e.setPhaseVoltageL2(parseDouble(value));
//                    break;
//
//                case "phaseVoltageL3":
//                    e.setPhaseVoltageL3(parseDouble(value));
//                    break;
//
//                case "blockEnergyKwhImport":
//                    e.setBlockEnergyKwhImport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKwhExport":
//                    e.setBlockEnergyKwhExport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKvahImport":
//                    e.setBlockEnergyKvahImport(parseDouble(value));
//                    break;
//
//                case "blockEnergyKvahExport":
//                    e.setBlockEnergyKvahExport(parseDouble(value));
//                    break;
//            }
//        }
//
//        // fallback (important)
//        if (interval == null) {
//            interval = mdasLdt != null ? mdasLdt : LocalDateTime.now();
//            id.setIntervalDatetime(interval);
//        }
//
//        e.setId(id);
//        e.setMdasDatetime(mdasLdt);
//
//        return e;
//    }
//    
//    private Double parseDouble(Object val) {
//        try {
//            return val != null ? Double.valueOf(val.toString()) : null;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    
//
//    private static LocalDateTime parseToLdt(String str) {
//        try {
//            Instant ins = Instant.parse(str);
//            return LocalDateTime.ofInstant(ins, ZoneId.systemDefault());
//        } catch (Exception ignored) {}
//
//        try {
//            Date d = DF.get().parse(str);
//            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
//        } catch (Exception ignored) {}
//
//        return null;
//    }
//}



