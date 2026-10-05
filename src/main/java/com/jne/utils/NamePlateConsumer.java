package com.jne.utils;




import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.NamePlate;
import com.jne.model_Id.NamePlateId;
import com.jne.repo.NamePlateRepository;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class NamePlateConsumer {

    private static final Logger log = LoggerFactory.getLogger(NamePlateConsumer.class);

    // ================== config ==================
    @Value("${kafka.nameplate.group-id}")
    private String groupId;

    @Autowired
    private NamePlateRepository repository;

    @Autowired
    private KafkaCommandMetrics commandMetrics;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ================== tunables (delta-style) ==================
    private static final int RETRY_ATTEMPTS      = 3;
    private static final long RETRY_DELAY_MS     = 5000L;

    private static final int BUFFER_SIZE         = 2000;

    // parallelism
    private static final int WRITE_CONCURRENCY   = 16;

    // backlog thresholds
    private static final int MAX_PENDING_INSERTS = 20000;
    private static final int RESUME_THRESHOLD    = 10000;
    private static final int MAX_QUEUE_SIZE      = 25000;

    // feature flags
    @Value("${feature.queue-writer.enabled:true}")
    private boolean queueWriterEnabled;

    @Value("${feature.queue.batch-size:1000}")
    private int QUEUE_BATCH;

    @Value("${feature.queue.flush-ms:200}")
    private long QUEUE_FLUSH_MS;

    @Value("${feature.queue.concurrency:4}")
    private int QUEUE_CONCURRENCY;

    // global cassandra in-flight limiter (same style as event consumers)
    private static final int PG_MAX_IN_FLIGHT = 200;

    private static final AtomicInteger PG_IN_FLIGHT =
            new AtomicInteger(0);

    // runtime state
    private final ConcurrentLinkedQueue<NamePlate> npQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger pendingInserts             = new AtomicInteger(0);
    private final AtomicBoolean paused                     = new AtomicBoolean(false);

    private ExecutorService npWorkers;

    // date parser
    private static final ThreadLocal<SimpleDateFormat> DF =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // =========================================================
    // START WRITER POOL
    // =========================================================
    @PostConstruct
    public void startWriterPool() {
        if (!queueWriterEnabled) {
            log.info("ℹ️ NamePlate queue-writer disabled. Will direct-save in listener.");
            return;
        }

        npWorkers = Executors.newFixedThreadPool(
            QUEUE_CONCURRENCY,
            r -> new Thread(r, "nameplate-writer")
        );

        for (int i = 0; i < QUEUE_CONCURRENCY; i++) {
            npWorkers.submit(this::writerLoop);
        }

        log.info("✅ NamePlate writer pool started (batch={}, flushMs={}, poolSize={})",
            QUEUE_BATCH, QUEUE_FLUSH_MS, QUEUE_CONCURRENCY);
    }

    @PreDestroy
    public void stopWriterPool() {
        if (!queueWriterEnabled) return;
        log.info("🛑 Stopping NamePlate writer pool...");
        if (npWorkers != null) npWorkers.shutdownNow();
    }

    private void writerLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int wrote = drainAndPersistBatch(QUEUE_BATCH)
                        .onErrorReturn(0)
                        .block();
                if (wrote == 0) {
                    Thread.sleep(QUEUE_FLUSH_MS);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                log.error("❌ NamePlate writer crashed, continuing", t);
            }
        }
    }

    // =========================================================
    // KAFKA LISTENER (delta-style: parse → enqueue → ack)
    // =========================================================
    @KafkaListener(
        id = "NamePlateListener",
        topics = "NamePlate",
        groupId = "${kafka.nameplate.group-id}",
        concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(String message, Acknowledgment ack) {

        Mono.fromCallable(() -> objectMapper.readTree(message))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(e -> {
                log.error("❌ Invalid JSON in NamePlate message: {}", e.getMessage(), e);
                return Mono.empty();
            })
            .flatMapMany(root -> {
                // build NamePlate entity (same as before)
                String meterType  = root.path("meterType").asText(null);
                String systemTime = root.path("systemTime").asText(null);
                String meterNoRaw = root.path("meterNo").asText(null);

                final Date mdasDateTime;
                try {
                    mdasDateTime = (systemTime == null || systemTime.isBlank())
                        ? new Date()
                        : DF.get().parse(systemTime);
                } catch (Exception e) {
                    log.error("❌ Failed to parse systemTime: {} -> using current date", systemTime, e);
                    return Flux.empty();
                }

                JsonNode dataNode = root.path("data");
                if (!(dataNode.isArray() && dataNode.size() > 0)) {
                    log.warn("⚠️ No OBIS data found in Kafka message for meter {}", meterNoRaw);
                    return Flux.empty();
                }

                JsonNode obisData = dataNode.get(0);
                List<String> obisCodes = objectMapper.convertValue(
                    obisData.path("1"),
                    new TypeReference<List<String>>() {}
                );
                List<Object> obisVals = objectMapper.convertValue(
                    obisData.path("2"),
                    new TypeReference<List<Object>>() {}
                );

                NamePlate entity = new NamePlate();

                entity.setMeterType(meterType);
                entity.setReadingType(1);


                entity.setOwnerName(
                    ObisFieldMapping.resolveOwner(
                        meterNoRaw == null ? "" : meterNoRaw.trim()
                    )
                );

                Map<String, String> mapping =
                        ObisFieldMapping.getNameplateFieldMappingSinglePhase();

                mapObisDataToEntity(entity, obisCodes, obisVals, mapping);

                // normalize IDs
                String dev = normalize(entity.getMeterSerialNumber());
                String met = normalize(meterNoRaw);

                if (met.isEmpty()) met = dev;
                if (dev.isEmpty()) dev = met;

                if (dev.isEmpty()) {
                    log.warn("Skipping...");
                    return Flux.empty();
                }

                LocalDateTime mdas =
                        mdasDateTime.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();

                entity.setId(
                        new NamePlateId(dev, mdas)
                );

                entity.setMeterSerialNumber(met);

                entity.setStatus(
                        dev.equalsIgnoreCase(met)
                                ? "VALID"
                                : "INVALID"
                );

                if (queueWriterEnabled) {
                    enqueueEntity(entity);
                    return Flux.just(true);
                } else {
                    return insertOne(entity).flux();
                }
            })

            // same as other delta consumers: buffer, but fail if full → no ack → redelivery
            .onBackpressureBuffer(
                BUFFER_SIZE,
                dropped -> log.warn("⚠️ NamePlate buffer full — would have dropped: {}", dropped),
                BufferOverflowStrategy.ERROR
            )

            .then()
            .doOnSuccess(v -> {
                ack.acknowledge();
            })
            .doOnError(err -> {
                // no ack → Kafka redeliver
                log.error("❌ Reactive pipeline error while processing NamePlate", err);
                forcePause();
            })
            .subscribe();
    }

    // =========================================================
    // ENQUEUE LOGIC
    // =========================================================
    private void enqueueEntity(NamePlate e) {
        int totalQ = npQueue.size();
        if (totalQ > MAX_QUEUE_SIZE) {
            forcePause();
            log.warn("🧯 NamePlate totalQueue {} > {}, pausing intake", totalQ, MAX_QUEUE_SIZE);
        }
        npQueue.offer(e);
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed("NamePlate", 1);
    }

    // =========================================================
    // DRAIN + PERSIST (delta-style, per-item insertOne)
    // =========================================================
    private Mono<Integer> drainAndPersistBatch(int maxBatch) {
        List<NamePlate> batch = new ArrayList<>(maxBatch);
        for (int i = 0; i < maxBatch; i++) {
            NamePlate e = npQueue.poll();
            if (e == null) break;
            batch.add(e);
        }
        if (batch.isEmpty()) return Mono.just(0);
        return persistBatch(batch, "NamePlate");
    }

    private Mono<Integer> persistBatch(List<NamePlate> batch, String metricKey) {

        return Flux.fromIterable(batch)
            .flatMap(this::insertOne, WRITE_CONCURRENCY)
            .collectList()
            .map(results -> {
                int success = 0;
                for (int i = 0; i < results.size(); i++) {
                    Boolean ok = results.get(i);
                    if (Boolean.TRUE.equals(ok)) {
                        success++;
                    } else {
                        // write failed → requeue item
                        npQueue.offer(batch.get(i));
                    }
                }
                pendingInserts.addAndGet(-success);
                commandMetrics.incrementSaved(metricKey, success);
                return success;
            })
            .retryWhen(
                Retry.backoff(RETRY_ATTEMPTS, Duration.ofMillis(RETRY_DELAY_MS))
                    .maxBackoff(Duration.ofSeconds(10))
                    .doBeforeRetry(sig ->
                        log.warn("🔁 Retrying {} batch save: {}", metricKey, sig.failure().toString())
                    )
            )
            .onErrorResume(err -> {
                // if Cassandra really down → requeue whole batch
                
                batch.forEach(npQueue::offer);
                forcePause();
                return Mono.just(0);
            });
    }

    // =========================================================
    // INSERT ONE with in-flight guard (like other consumers)
    // =========================================================
    private Mono<Boolean> insertOne(NamePlate entity) {

        return Mono.fromCallable(() -> {

            NamePlateId id = entity.getId();

            int rows = repository.insertIgnore(
                    id.getDeviceSerialNumber(),
                    id.getMdasDatetime(),
                    entity.getMeterSerialNumber(),
                    entity.getDeviceId(),          // <-- added
                    entity.getMeterType(),
                    entity.getOwnerName(),
                    entity.getStatus(),
                    entity.getManufacturerName(),
                    entity.getManufacturerYear(),
                    entity.getFirmwareVersion(),
                    entity.getCategory(),
                    entity.getCurrentRatings()
            );

            log.info(
                    "device={} mdas={} rows={} deviceId={}",
                    id.getDeviceSerialNumber(),
                    id.getMdasDatetime(),
                    rows,
                    entity.getDeviceId()
            );

            // treat duplicate as success
            return true;

        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(ex -> {

            log.error(
                    "PostgreSQL insert failed device={} mdas={}",
                    entity.getId().getDeviceSerialNumber(),
                    entity.getId().getMdasDatetime(),
                    ex
            );

            return Mono.just(false);
        });
    }


    // =========================================================
    // CENTRAL THROTTLE (pause / resume)
    // =========================================================
    @Scheduled(fixedDelay = 1000)
    public void monitorAndThrottle() {
        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("NamePlateListener");
        if (c == null) return;

        int qBacklog     = npQueue.size();
        int totalBacklog = pendingInserts.get() + qBacklog;
        int inflight     = PG_IN_FLIGHT.get();

        // pause condition: backlog high OR cassandra busy
        if ((totalBacklog > MAX_PENDING_INSERTS
                || inflight > (PG_MAX_IN_FLIGHT * 0.8))
        		&& paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing NamePlateListener (backlog={} / inflight={})",
                totalBacklog, inflight);
            c.pause();
            return;
        }

        // resume condition: backlog low AND cassandra relaxed
        if (totalBacklog < RESUME_THRESHOLD
                && inflight < (PG_MAX_IN_FLIGHT * 0.5)
                && paused.compareAndSet(true, false)) {
            log.info("▶ Resuming NamePlateListener (backlog={} < {}, inflight={})",
                totalBacklog, RESUME_THRESHOLD, inflight);
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("NamePlateListener");
        if (c != null && paused.compareAndSet(false, true)) {
            log.warn("⏸ Force-pausing NamePlateListener: Cassandra unavailable / backlog high");
            c.pause();
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    

    private void mapObisDataToEntity(
        NamePlate entity,
        List<String> obisCodes,
        List<Object> obisValues,
        Map<String, String> mapping
    ) {
        if (obisCodes == null || obisValues == null) return;

        Map<String, Integer> obisSeenCount = new HashMap<>();
        int size = Math.min(obisCodes.size(), obisValues.size());

        for (int i = 0; i < size; i++) {
            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);

            int dupIndex = obisSeenCount.getOrDefault(obis, 0);
            obisSeenCount.put(obis, dupIndex + 1);

            String fieldName = mapping.get(obis);
            if (fieldName != null && value != null) {
                try {
                    Field field = entity.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if (field.getType() == Date.class) {
                        Date dateValue = DF.get().parse(value.toString());
                        field.set(entity, dateValue);
                    } else if (field.getType() == Double.class || field.getType() == double.class) {
                        field.set(entity, Double.parseDouble(value.toString()));
                    } else if (field.getType() == Integer.class || field.getType() == int.class) {
                        if (value instanceof Number) {
                            field.set(entity, ((Number) value).intValue());
                        } else {
                            field.set(entity, Integer.parseInt(value.toString()));
                        }
                    } else {
                        String s = normalize(value.toString());
                        if (!s.isEmpty()) {
                            field.set(entity, s);
                        }
                    }

                } catch (NoSuchFieldException nsf) {
                    log.warn("⚠️ Field not found on NamePlate: {} -> {}", fieldName, nsf.getMessage());
                } catch (Exception e) {
                    log.error("❌ Mapping failed for OBIS {}, Value {} => {}", obis, value, e.getMessage(), e);
                }
            }
        }
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim();
        return ("".equals(t) || "null".equalsIgnoreCase(t)) ? "" : t;
    }
}