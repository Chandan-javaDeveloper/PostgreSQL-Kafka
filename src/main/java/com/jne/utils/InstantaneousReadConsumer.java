package com.jne.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.InstantaneousData;
import com.jne.model.InstantaneousDataSinglePhase;
import com.jne.model.InstantaneousDataSinglePhaseId;
import com.jne.model.InstantaneousDataThreePhase;
import com.jne.model.InstantaneousDataThreePhaseId;
import com.jne.model_Id.InstantaneousDataId;
import com.jne.repo.InstantaneousDataRepository;
import com.jne.repo.InstantaneousSinglePhaseRepo;
import com.jne.repo.InstantaneousThreePhaseRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class InstantaneousReadConsumer {

    private static final Logger log = LoggerFactory.getLogger(InstantaneousReadConsumer.class);

    @Value("${feature.queue-writer.enabled:true}")
    private boolean queueWriterEnabled;

    @Value("${feature.queue.batch-size:1000}")
    private int QUEUE_BATCH;

    @Value("${feature.queue.flush-ms:200}")
    private long QUEUE_FLUSH_MS;

    @Value("${feature.queue.concurrency:4}")
    private int QUEUE_CONCURRENCY;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int WRITE_CONCURRENCY = 32;
    private static final int BUFFER_SIZE = 2000;

    private static final int MAX_PENDING_INSERTS = 20000;
    private static final int RESUME_THRESHOLD = 10000;
    private static final int MAX_QUEUE_SIZE = 25000;

    private static final String DT_OBIS = "0.0.1.0.0.255";

    private final AtomicInteger pendingInserts = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    private final ConcurrentLinkedQueue<InstantaneousDataSinglePhase> spQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InstantaneousDataThreePhase> tpQueue = new ConcurrentLinkedQueue<>();

    private final AtomicInteger spQSize = new AtomicInteger(0);
    private final AtomicInteger tpQSize = new AtomicInteger(0);

    private ExecutorService spWorkers;
    private ExecutorService tpWorkers;
    
    private final ConcurrentLinkedQueue<InstantaneousData> instQueue =
            new ConcurrentLinkedQueue<>();

    private final AtomicInteger instQSize = new AtomicInteger(0);

    private ExecutorService instWorkers;

    private final InstantaneousSinglePhaseRepo spRepo;
    private final InstantaneousThreePhaseRepo tpRepo;
    private final InstantaneousDataRepository instantaneousDataRepository;

    private final KafkaListenerEndpointRegistry registry;
    private final TransactionTemplate tx;
    private final KafkaCommandMetrics commandMetrics;

    public InstantaneousReadConsumer(
            InstantaneousSinglePhaseRepo spRepo,
            InstantaneousThreePhaseRepo tpRepo,
            InstantaneousDataRepository instantaneousDataRepository,
            KafkaListenerEndpointRegistry registry,
            PlatformTransactionManager txManager,
            KafkaCommandMetrics commandMetrics
    ) {

        this.spRepo = spRepo;
        this.tpRepo = tpRepo;
        this.instantaneousDataRepository = instantaneousDataRepository;
        this.registry = registry;
        this.tx = new TransactionTemplate(txManager);
        this.commandMetrics = commandMetrics;
    }

    // ---------- writers ----------
    @PostConstruct
    public void startWriters() {
        if (!queueWriterEnabled) {
            log.info("ℹ Instantaneous PG queue writers disabled.");
            return;
        }

        spWorkers = Executors.newFixedThreadPool(QUEUE_CONCURRENCY, r -> new Thread(r, "inst-pg-sp-writer"));
        tpWorkers = Executors.newFixedThreadPool(QUEUE_CONCURRENCY, r -> new Thread(r, "inst-pg-tp-writer"));
        instWorkers = Executors.newFixedThreadPool(QUEUE_CONCURRENCY,r -> new Thread(r, "inst-pg-drift-writer"));

        for (int i = 0; i < QUEUE_CONCURRENCY; i++) {
            spWorkers.submit(this::spWorkerLoop);
            tpWorkers.submit(this::tpWorkerLoop);
        }
        
        

        for(int i=0;i<QUEUE_CONCURRENCY;i++){
            instWorkers.submit(this::instWorkerLoop);
        }

        log.info("✅ Instantaneous PG writers started (batch={}, flush={}ms, concurrency={})",
                QUEUE_BATCH, QUEUE_FLUSH_MS, QUEUE_CONCURRENCY);
    }

    @PreDestroy
    public void stopWriters() {
        if (!queueWriterEnabled) return;
        if (spWorkers != null) spWorkers.shutdownNow();
        if (tpWorkers != null) tpWorkers.shutdownNow();
        if(instWorkers!=null)instWorkers.shutdownNow();
        stopAndFlush(); // ✅ flush remaining data on shutdown
    }

    // ✅ flush remaining on shutdown so no data lost
    private void stopAndFlush() {
        try {
            log.warn("🧹 Flushing remaining Instantaneous PG queues...");
            for (int i = 0; i < 50; i++) { // max loops
                int a = drainAndSaveSPBatchBlocking(QUEUE_BATCH).block();
                int b = drainAndSaveTPBatchBlocking(QUEUE_BATCH).block();
                int c = drainAndSaveInstBatchBlocking(QUEUE_BATCH).block();
                if ((a + b+ c) == 0) break;
            }
        } catch (Exception e) {
            log.error("❌ Flush failed", e);
        }
    }

    private void spWorkerLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int wrote = drainAndSaveSPBatchBlocking(QUEUE_BATCH).block();
                if (wrote == 0) Thread.sleep(QUEUE_FLUSH_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                log.error("❌ Instantaneous PG SP worker crashed", t);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    private void tpWorkerLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int wrote = drainAndSaveTPBatchBlocking(QUEUE_BATCH).block();
                if (wrote == 0) Thread.sleep(QUEUE_FLUSH_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                log.error("❌ Instantaneous PG TP worker crashed", t);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ---------- kafka listener ----------
    @KafkaListener(
            id = "InstantaneousReadPG",
            topics = "InstantaneousRead",
            groupId = "${kafka.instant.group-id}",
            concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {
        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        Flux.fromIterable(messages)
                .flatMap(msg -> Mono.fromCallable(() -> objectMapper.readTree(msg)), WRITE_CONCURRENCY)
                .flatMap(this::parseAndEnqueue, WRITE_CONCURRENCY)
                .onBackpressureBuffer(
                        BUFFER_SIZE,
                        dropped -> log.warn("⚠ Instantaneous PG buffer full — dropped chunk."),
                        BufferOverflowStrategy.DROP_OLDEST
                )
                .then()
                .doOnSuccess(v -> ack.acknowledge())
                .doOnError(err -> log.error("❌ Instantaneous PG pipeline error", err))
                .subscribe();
    }

    // ---------- parse + enqueue ----------
    private Flux<Object> parseAndEnqueue(JsonNode root) {
        try {
            String meterType = root.path("meterType").asText(null);
            String meterNo   = root.path("meterNo").asText(null);
            String systemTime = root.path("systemTime").asText(null);
            String trackingId = root.path("trackingId").asText(null);

            LocalDateTime mdas = parseToLdt(systemTime);

            JsonNode dataNode = root.path("data");
            if (!(dataNode.isArray() && dataNode.size() > 0)) return Flux.empty();

            JsonNode obisData = dataNode.get(0);

            List<String> obisCodes = objectMapper.convertValue(obisData.path("1"),
                    new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});

            List<String> fieldKeys = new ArrayList<>();
            obisData.fieldNames().forEachRemaining(fk -> { if (!"1".equals(fk)) fieldKeys.add(fk); });

            List<Object> built = new ArrayList<>();

            if ("Single Phase".equalsIgnoreCase(meterType)) {
            	for (String fk : fieldKeys) {

            	    List<Object> vals =
            	            objectMapper.convertValue(
            	                    obisData.path(fk),
            	                    new TypeReference<List<Object>>() {});

            	    InstantaneousDataSinglePhase e =
            	            buildSP(
            	                    meterNo,
            	                    mdas,
            	                    trackingId,
            	                    obisCodes,
            	                    vals
            	            );

            	    enqueueSP(e);
            	    built.add(e);

            	    // Save drift only once
            	    if ("2".equals(fk)) {

            	        InstantaneousData drift =
            	                buildInstantaneousData(
            	                        meterNo,
            	                        mdas,
            	                        obisCodes,
            	                        vals
            	                );

            	        enqueueInstantaneous(drift);
            	    }
            	}
            } else {
                for (String fk : fieldKeys) {
                    List<Object> vals = objectMapper.convertValue(obisData.path(fk),
                            new com.fasterxml.jackson.core.type.TypeReference<List<Object>>() {});

                    InstantaneousDataThreePhase e = buildTP(meterNo, mdas, trackingId, obisCodes, vals);
                    enqueueTP(e);
                    built.add(e);
                    
                    if("2".equals(fk)){

                        InstantaneousData drift =
                                buildInstantaneousData(
                                        meterNo,
                                        mdas,
                                        obisCodes,
                                        vals
                                );

                        enqueueInstantaneous(drift);
                    }
                }
            }

            return built.isEmpty() ? Flux.empty() : Flux.fromIterable(built);

        } catch (Exception e) {
            log.error("❌ Instantaneous PG parse failed: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    // ---------- enqueue ----------
    private void enqueueSP(InstantaneousDataSinglePhase e) {
        int total = spQSize.get() + tpQSize.get();
        if (total > MAX_QUEUE_SIZE) forcePause();

        spQueue.offer(e);
        spQSize.incrementAndGet();
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed("Instantaneous-PG-1P", 1);
    }

    private void enqueueTP(InstantaneousDataThreePhase e) {
        int total = spQSize.get() + tpQSize.get();
        if (total > MAX_QUEUE_SIZE) forcePause();

        tpQueue.offer(e);
        tpQSize.incrementAndGet();
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed("Instantaneous-PG-3P", 1);
    }
    
    private void enqueueInstantaneous(InstantaneousData e) {

        int total = spQSize.get() + tpQSize.get() + instQSize.get();

        if (total > MAX_QUEUE_SIZE) {
            forcePause();
        }

        instQueue.offer(e);
        instQSize.incrementAndGet();
        pendingInserts.incrementAndGet();

        // Dashboard Consumed Count
        commandMetrics.incrementConsumed("InstantaneousData", 1);
    }

    // ---------- drain + save ----------
    private Mono<Integer> drainAndSaveSPBatchBlocking(int max) {
        List<InstantaneousDataSinglePhase> batch = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            InstantaneousDataSinglePhase e = spQueue.poll();
            if (e == null) break;
            spQSize.decrementAndGet();
            batch.add(e);
        }
        if (batch.isEmpty()) return Mono.just(0);

        pendingInserts.addAndGet(-batch.size());

        return Mono.fromCallable(() -> tx.execute(status -> {
                    spRepo.saveAll(batch);
                    return batch.size();
                }))
                .doOnNext(n -> commandMetrics.incrementSaved("Instantaneous-PG-1P", n))
                .onErrorResume(err -> {
                    log.error("❌ PG SP batch failed, requeue {}: {}", batch.size(), err.toString(), err);
                    for (var e : batch) { spQueue.offer(e); spQSize.incrementAndGet(); }
                    pendingInserts.addAndGet(batch.size());
                    forcePause();
                    return Mono.just(0);
                });
    }

    private Mono<Integer> drainAndSaveTPBatchBlocking(int max) {
        List<InstantaneousDataThreePhase> batch = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            InstantaneousDataThreePhase e = tpQueue.poll();
            if (e == null) break;
            tpQSize.decrementAndGet();
            batch.add(e);
        }
        if (batch.isEmpty()) return Mono.just(0);

        pendingInserts.addAndGet(-batch.size());

        return Mono.fromCallable(() -> tx.execute(status -> {
                    tpRepo.saveAll(batch);
                    return batch.size();
                }))
                .doOnNext(n -> commandMetrics.incrementSaved("Instantaneous-PG-3P", n))
                .onErrorResume(err -> {
                    log.error("❌ PG TP batch failed, requeue {}: {}", batch.size(), err.toString(), err);
                    for (var e : batch) { tpQueue.offer(e); tpQSize.incrementAndGet(); }
                    pendingInserts.addAndGet(batch.size());
                    forcePause();
                    return Mono.just(0);
                });
    }

    // ---------- pause/resume ----------
    @Scheduled(fixedDelay = 1000)
    public void monitor() {
        MessageListenerContainer c = registry.getListenerContainer("InstantaneousReadPG");
        if (c == null) return;

        int q = spQSize.get() + tpQSize.get();
        int total = pendingInserts.get() + q;

        if (total > MAX_PENDING_INSERTS && paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing InstantaneousReadPG (backlog={} > {})", total, MAX_PENDING_INSERTS);
            c.pause();
            return;
        }

        if (total < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
            log.info("▶ Resuming InstantaneousReadPG (backlog={} < {})", total, RESUME_THRESHOLD);
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c = registry.getListenerContainer("InstantaneousReadPG");
        if (c != null && paused.compareAndSet(false, true)) {
            log.warn("⏸ Force-pausing InstantaneousReadPG");
            c.pause();
        }
    }

    // ---------- builders ----------
    private InstantaneousDataThreePhase buildTP(String meterNo, LocalDateTime mdas, String trackingId,
                                               List<String> obisCodes, List<Object> obisValues) {
        LocalDateTime meterDt = extractObisDatetime(obisCodes, obisValues, mdas);

        InstantaneousDataThreePhaseId id = new InstantaneousDataThreePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setDatetime(meterDt);

        InstantaneousDataThreePhase e = new InstantaneousDataThreePhase();
        e.setId(id);

        // set mandatory fields
        e.setMdasDatetime(mdas);
        e.setTrackingId(trackingId);
        e.setReadingType(1);


        // mapping should NOT set meter_datetime again
        Map<String, String> mapping = ObisFieldMapping.getInstantReadFieldMappingThreePhase();
        mapObisDataToEntity(e, obisCodes, obisValues, mapping);

        return e;
    }

    private InstantaneousDataSinglePhase buildSP(String meterNo, LocalDateTime mdas, String trackingId,
                                                List<String> obisCodes, List<Object> obisValues) {
        LocalDateTime meterDt = extractObisDatetime(obisCodes, obisValues, mdas);

        InstantaneousDataSinglePhaseId id = new InstantaneousDataSinglePhaseId();
        id.setDeviceSerialNumber(meterNo);
        id.setDatetime(meterDt);

        InstantaneousDataSinglePhase e = new InstantaneousDataSinglePhase();
        e.setId(id);

        e.setMdasDatetime(mdas);
        e.setTrackingId(trackingId);
        e.setReadingType(1);


        Map<String, String> mapping = ObisFieldMapping.getInstantReadFieldMappingSinglePhase();
        mapObisDataToEntity(e, obisCodes, obisValues, mapping);

        return e;
    }
    
    private InstantaneousData buildInstantaneousData(
            String meterNo,
            LocalDateTime mdas,
            List<String> obisCodes,
            List<Object> obisValues) {

        LocalDateTime meterTime =
                extractObisDatetime(obisCodes, obisValues, mdas);

        InstantaneousDataId id =
                new InstantaneousDataId();

        id.setDeviceSerialNumber(meterNo);
        id.setMdasDatetime(mdas);

        InstantaneousData data =
                new InstantaneousData();

        data.setId(id);

        data.setMeterDatetime(meterTime);

        long drift =
                Duration.between(mdas, meterTime).getSeconds();

        data.setTimedrift((int) drift);

        return data;
    }
    
    private void instWorkerLoop(){

        while(!Thread.currentThread().isInterrupted()){

            try{

                int wrote =
                        drainAndSaveInstBatchBlocking(QUEUE_BATCH).block();

                if(wrote==0){

                    Thread.sleep(QUEUE_FLUSH_MS);

                }

            }catch (InterruptedException ex){

                Thread.currentThread().interrupt();

            }catch (Throwable t){

                log.error("Instantaneous Drift Worker",t);

            }

        }

    }
    
    private Mono<Integer> drainAndSaveInstBatchBlocking(int max){

        List<InstantaneousData> batch =
                new ArrayList<>(max);

        for(int i=0;i<max;i++){

            InstantaneousData e =
                    instQueue.poll();

            if(e==null)
                break;

            instQSize.decrementAndGet();

            batch.add(e);

        }

        if(batch.isEmpty())
            return Mono.just(0);

        pendingInserts.addAndGet(-batch.size());

        return Mono.fromCallable(() ->

                tx.execute(status -> {

                    instantaneousDataRepository.saveAll(batch);

                    return batch.size();

                })

        );
    }

    // ---------- common mapping (same as DailyLP) ----------
    private void mapObisDataToEntity(Object entity, List<String> obisCodes, List<Object> obisValues,
                                     Map<String, String> mapping) {
        if (obisCodes == null || obisValues == null) return;
        int size = Math.min(obisCodes.size(), obisValues.size());

        for (int i = 0; i < size; i++) {
            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);
            if (value == null) continue;

            // ✅ skip datetime obis because we already used it for PK
            if (DT_OBIS.equals(obis)) continue;

            String fieldName = mapping.get(obis);
            if (fieldName == null) continue;

            try {
                Field f = resolveField(entity.getClass(), fieldName);
                Class<?> t = f.getType();

                if (t == Double.class || t == double.class) {
                    f.set(entity, Double.parseDouble(value.toString()));
                } else if (t == Integer.class || t == int.class) {
                    f.set(entity, (int) Double.parseDouble(value.toString()));
                } else if (t == LocalDateTime.class) {
                    f.set(entity, parseToLdt(value.toString()));
                } else {
                    f.set(entity, value.toString());
                }
            } catch (Exception ex) {
                log.debug("Mapping failed obis={} field={} err={}", obis, fieldName, ex.toString());
            }
        }
    }

    private static Field resolveField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException ignore) {}

        String camel = snakeToCamel(name);
        Field f = clazz.getDeclaredField(camel);
        f.setAccessible(true);
        return f;
    }

    private static String snakeToCamel(String s) {
        if (s == null || !s.contains("_")) return s;
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char c : s.toCharArray()) {
            if (c == '_') { up = true; continue; }
            sb.append(up ? Character.toUpperCase(c) : c);
            up = false;
        }
        return sb.toString();
    }

    private static LocalDateTime extractObisDatetime(List<String> obisCodes, List<Object> obisValues, LocalDateTime fallback) {
        try {
            int n = Math.min(obisCodes.size(), obisValues.size());
            for (int i = 0; i < n; i++) {
                if (DT_OBIS.equals(obisCodes.get(i)) && obisValues.get(i) != null) {
                    return parseToLdt(obisValues.get(i).toString());
                }
            }
        } catch (Exception ignore) {}
        return fallback != null ? fallback : LocalDateTime.now();
    }

    private static LocalDateTime parseToLdt(String str) {
        if (str == null || str.isBlank()) return LocalDateTime.now();
        String s = str.trim();

        try {
            if (s.contains("T")) {
                return LocalDateTime.ofInstant(java.time.Instant.parse(s), ZoneId.systemDefault());
            }
        } catch (Exception ignore) {}

        try {
            s = s.replace("T", " ").replace("Z", "");
            s = s.replaceAll("\\.\\d+", "");
            s = s.replaceAll("[^0-9:\\- ]", " ").replaceAll("\\s+", " ").trim();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setLenient(false);
            Date d = df.parse(s);
            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}


