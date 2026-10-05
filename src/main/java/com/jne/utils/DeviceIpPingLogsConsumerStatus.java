//package com.jne.utils;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.kafka.listener.MessageListenerContainer;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jne.model.DevicesIpPingLogs;
//import com.jne.repo.DevicesIpPingLogsRepository;
//import com.jne.service.utils.KafkaCommandMetrics;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import reactor.core.publisher.BufferOverflowStrategy;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.util.retry.Retry;
//
//@Component
//@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
//@EnableScheduling
//public class DeviceIpPingLogsConsumerStatus {
//
//    private static final Logger log = LoggerFactory.getLogger(DeviceIpPingLogsConsumerStatus.class);
//
//    @Value("${kafka.deviceippinglogstatus.group-id}")
//    private String groupId;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private DevicesIpPingLogsRepository repository;
//
//    @Autowired
//    private KafkaCommandMetrics commandMetrics;
//
//    @Autowired
//    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//    // ================== QUEUE / FLOW CONTROL TUNABLES ==================
//
//    private final ConcurrentLinkedQueue<DevicesIpPingLogs> pingQueue = new ConcurrentLinkedQueue<>();
//
//    @Value("${feature.queue.batch-size:1000}")
//    private int QUEUE_BATCH;
//
//    @Value("${feature.queue.flush-ms:500}")
//    private long QUEUE_FLUSH_MS;
//
//    @Value("${feature.queue.concurrency:4}")
//    private int QUEUE_CONCURRENCY;
//
//    private static final int WRITE_CONCURRENCY = 16;
//    private static final int BUFFER_SIZE       = 5_000;
//
//    private static final int MAX_PENDING_INSERTS = 20_000;
//    private static final int RESUME_THRESHOLD    = 10_000;
//    private static final int MAX_QUEUE_SIZE      = 25000;
//
//    private static final int  MAX_RETRY      = 3;
//    private static final long RETRY_DELAY_MS = 5_000L;
//
//    // logical “to be written” counter
//    private final AtomicInteger pendingInserts = new AtomicInteger(0);
//    private final AtomicBoolean paused         = new AtomicBoolean(false);
//
//    // per–consumer Cassandra in-flight guard (NOT static!)
//    private final int cassMaxInFlight = 200;
//    private final AtomicInteger cassInFlight = new AtomicInteger(0);
//
//    private ExecutorService pingWorkers;
//
//    private static final DateTimeFormatter TS_FMT =
//        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    // ================== LIFECYCLE ==================
//    @PostConstruct
//    public void startWorkers() {
//        pingWorkers = Executors.newFixedThreadPool(
//            QUEUE_CONCURRENCY,
//            r -> new Thread(r, "ping-writer")
//        );
//
//        for (int i = 0; i < QUEUE_CONCURRENCY; i++) {
//            pingWorkers.submit(this::pingWorkerLoop);
//        }
//
//        log.info("✅ PingStatus writer pool started (batch={}, sleep={}ms, concurrency={})",
//            QUEUE_BATCH, QUEUE_FLUSH_MS, QUEUE_CONCURRENCY);
//    }
//
//    @PreDestroy
//    public void stopWorkers() {
//        if (pingWorkers != null) {
//            log.info("🛑 Stopping PingStatus writer pool...");
//            pingWorkers.shutdownNow();
//        }
//    }
//
//    // ================== WORKER LOOP ==================
//    private void pingWorkerLoop() {
//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                Integer written = drainAndPersistPingBatch(QUEUE_BATCH)
//                    .onErrorReturn(0)
//                    .block();
//
//                if (written == null || written == 0) {
//                    Thread.sleep(QUEUE_FLUSH_MS);
//                }
//
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//            } catch (Throwable t) {
//                log.error("❌ pingWorker crashed, continuing", t);
//            }
//        }
//    }
//
//    // ================== KAFKA LISTENER ==================
//    @KafkaListener(
//        id = "PingStatusListener",
//        topics = "ping-status",
//        groupId = "${kafka.deviceippinglogstatus.group-id}",
//        concurrency = "${feature.queue.concurrency:6}"
//    )
//    public void consume(List<String> messages, Acknowledgment ack) {
//
//        if (messages == null || messages.isEmpty()) {
//            ack.acknowledge();
//            return;
//        }
//
//        Flux.fromIterable(messages)
//            .flatMap(msg -> {
//                try {
//                    JsonNode root = objectMapper.readTree(msg);
//                    JsonNode data = root.get("data");
//
//                    if (data == null) {
//                        log.error("❌ Dropping message, missing `data`: {}", msg);
//                        return Mono.<DevicesIpPingLogs>empty();
//                    }
//
//                    String trackingId       = getText(data, "trackingId");
//                    String deviceNo         = getText(data, "deviceNo");
//                    String status           = getText(data, "status");
//                    String mdasDateTimeStr  = getText(data, "mdasDateTime");
//                    String timestamp        = getText(root, "timestamp");
//                    String reasonMsg        = getText(root, "message");
//
//                    if (trackingId == null || deviceNo == null) {
//                        log.error("❌ Dropping ping msg, missing trackingId/deviceNo: {}", msg);
//                        return Mono.<DevicesIpPingLogs>empty();
//                    }
//
//                    DevicesIpPingLogs entity = new DevicesIpPingLogs();
//                    entity.setTrackingId(trackingId);
//                    entity.setDeviceSerialNumber(deviceNo);
//                    entity.setStatus(status != null ? status : "UNKNOWN");
//                    entity.setOwnerName("MZR");
//                    entity.setReason(reasonMsg != null ? reasonMsg : "Ping response");
//
//                    // mdas_datetime
//                    if (mdasDateTimeStr != null) {
//                        try {
//                            LocalDateTime ldt = LocalDateTime.parse(mdasDateTimeStr, TS_FMT);
//                            Instant inst = ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant();
//                            entity.setMdasDatetime( LocalDateTime.ofInstant(inst,ZoneId.systemDefault()   )
//                            );
//                        } catch (Exception e) {
//                            log.warn("⚠️ Bad mdasDateTime '{}': {}", mdasDateTimeStr, e.toString());
//                        }
//                    }
//
//                    // completion time only for success/failed
//                    if (("success".equalsIgnoreCase(status) || "failed".equalsIgnoreCase(status))
//                        && timestamp != null) {
//                        try {
//                            LocalDateTime ldt = LocalDateTime.parse(timestamp, TS_FMT);
//                            Instant inst = ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant();
//                            entity.setCommandCompletionDatetime( LocalDateTime.ofInstant(inst, ZoneId.systemDefault() )
//                            );
//                        } catch (Exception e) {
//                            log.warn("⚠️ Bad completion ts '{}': {}", timestamp, e.toString());
//                        }
//                    }
//
//                    // first time
//                    entity.setTotAttempts(1);
//
//                    // enqueue (like delta)
//                    guardAndEnqueuePing(entity);
//
//                    // metric: we accepted this message
//                    commandMetrics.incrementConsumed("PingStatus", 1);
//
//                    return Mono.just(entity);
//
//                } catch (Exception e) {
//                    log.error("❌ Error parsing ping message: {}", msg, e);
//                    return Mono.<DevicesIpPingLogs>empty();
//                }
//            })
//            .onBackpressureBuffer(
//                BUFFER_SIZE,
//                dropped -> log.warn("⚠ PingStatus buffer full — dropped oldest chunk."),
//                BufferOverflowStrategy.DROP_OLDEST
//            )
//            .then()
//            .doOnSuccess(v -> ack.acknowledge())
//            .doOnError(err -> {
//                log.error("❌ Reactive pipeline error while processing ping-status batch", err);
//                forcePause();
//            })
//            .subscribe();
//    }
//
//    // ================== ENQUEUE ==================
//    private void guardAndEnqueuePing(DevicesIpPingLogs e) {
//        int totalQueueSize = pingQueue.size();
//
//        if (totalQueueSize > MAX_QUEUE_SIZE) {
//            forcePause();
//            log.warn("🧯 PingStatus queue backlog {} exceeded {}, pausing intake",
//                totalQueueSize, MAX_QUEUE_SIZE);
//        }
//
//        pingQueue.offer(e);
//        pendingInserts.incrementAndGet();
//    }
//
//    // ================== DRAIN & SAVE WORKER SIDE ==================
//    private Mono<Integer> drainAndPersistPingBatch(int maxBatch) {
//        List<DevicesIpPingLogs> batch = new ArrayList<>(maxBatch);
//        for (int i = 0; i < maxBatch; i++) {
//        	DevicesIpPingLogs e = pingQueue.poll();
//            if (e == null) break;
//            batch.add(e);
//        }
//
//        if (batch.isEmpty()) {
//            return Mono.just(0);
//        }
//
//        return persistPingBatch(batch);
//    }
//
//    private Mono<Integer> persistPingBatch(List<DevicesIpPingLogs> batch) {
//
//        return Flux.fromIterable(batch)
//            .flatMap(entity ->
//                upsertPingEntityGuarded(entity)
//                    .map(ok -> new PersistResult<>(entity, ok)),
//                WRITE_CONCURRENCY
//            )
//            .collectList()
//            .flatMap(results -> {
//                int successCount = 0;
//
//                for (PersistResult<DevicesIpPingLogs> r : results) {
//                    if (r.ok) {
//                        successCount++;
//                    } else {
//                        // write failed → requeue
//                        requeuePing(r.entity);
//                    }
//                }
//
//                if (successCount > 0) {
//                    commandMetrics.incrementSaved("PingStatus", successCount);
//                    pendingInserts.addAndGet(-successCount);
//                }
//
//                return Mono.just(successCount);
//            })
//            .retryWhen(
//                Retry.backoff(MAX_RETRY, Duration.ofMillis(RETRY_DELAY_MS))
//                    .maxBackoff(Duration.ofSeconds(10))
//                    .doBeforeRetry(sig ->
//                        log.warn("🔁 Retrying PingStatus batch save: {}", sig.failure().toString())
//                    )
//            )
//            .onErrorResume(err -> {
//                // Cassandra really down → requeue everything, pause
//
//                for (DevicesIpPingLogs e : batch) {
//                    requeuePing(e);
//                }
//                forcePause();
//
//                return Mono.just(0);
//            });
//    }
//
//    private void requeuePing(DevicesIpPingLogs e) {
//        pingQueue.offer(e);
//    }
//
//    private static class PersistResult<T> {
//        final T entity;
//        final boolean ok;
//        PersistResult(T entity, boolean ok) {
//            this.entity = entity;
//            this.ok = ok;
//        }
//    }
//
//    // ================== UPSERT with in-flight guard ==================
//    private Mono<Boolean> upsertPingEntityGuarded(DevicesIpPingLogs incoming) {
//        return Mono.defer(() -> {
//            int now = cassInFlight.incrementAndGet();
//            if (now > cassMaxInFlight) {
//                cassInFlight.decrementAndGet();
//                // soft fail -> will be requeued by caller
//                return Mono.just(false);
//            }
//
//            return upsertPingEntity(incoming)
//                .doFinally(sig -> cassInFlight.decrementAndGet());
//        });
//    }
//
//    private Mono<Boolean> upsertPingEntity(DevicesIpPingLogs incoming) {
//
//        return Mono.fromCallable(() -> {
//
//            Optional<DevicesIpPingLogs> existingOpt =
//                    repository.findByDeviceSerialNumberAndTrackingId(
//                            incoming.getDeviceSerialNumber(),
//                            incoming.getTrackingId()
//                    );
//
//            if (existingOpt.isPresent()) {
//
//                DevicesIpPingLogs existing = existingOpt.get();
//
//                int attempts =
//                        existing.getTotAttempts() == null
//                        ? 0
//                        : existing.getTotAttempts();
//
//                incoming.setTotAttempts(attempts + 1);
//
//            } else {
//
//                incoming.setTotAttempts(1);
//            }
//
//            repository.save(incoming);
//
//            return true;
//
//        }).onErrorReturn(false);
//    }
//
//    // ================== BACKLOG MONITOR (pause / resume Kafka) ==================
//    @Scheduled(fixedDelay = 1000)
//    public void monitorAndThrottle() {
//        MessageListenerContainer c =
//            kafkaListenerEndpointRegistry.getListenerContainer("PingStatusListener");
//        if (c == null) return;
//
//        int qBacklog     = pingQueue.size();
//        int totalBacklog = pendingInserts.get() + qBacklog;
//        int inflight     = cassInFlight.get();
//
//        if ((totalBacklog > MAX_PENDING_INSERTS || inflight > (cassMaxInFlight * 0.8))
//                && paused.compareAndSet(false, true)) {
//            log.warn("⏸ Pausing PingStatus listener (backlog={} / inflight={})",
//                totalBacklog, inflight);
//            c.pause();
//            return;
//        }
//
//        if (totalBacklog < RESUME_THRESHOLD
//                && inflight < (cassMaxInFlight * 0.5)
//                && paused.compareAndSet(true, false)) {
//            log.info("▶ Resuming PingStatus listener (backlog={} < {}, inflight={})",
//                totalBacklog, RESUME_THRESHOLD, inflight);
//            c.resume();
//        }
//    }
//
//    private void forcePause() {
//        MessageListenerContainer c =
//            kafkaListenerEndpointRegistry.getListenerContainer("PingStatusListener");
//        if (c != null && paused.compareAndSet(false, true)) {
//            log.warn("⏸ Force-pausing PingStatus listener: Cassandra unavailable / backlog high");
//            c.pause();
//        }
//    }
//
//    // ================== helpers ==================
//    private String getText(JsonNode root, String field) {
//        return (root != null && root.hasNonNull(field)) ? root.get(field).asText() : null;
//    }
//
//    
//}
