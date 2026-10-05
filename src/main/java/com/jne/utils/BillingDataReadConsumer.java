package com.jne.utils;



import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jne.model.BillingDataSinglePhase;
import com.jne.model.BillingDataSinglePhaseId;
import com.jne.model.BillingDataThreePhase;
import com.jne.model.BillingDataThreePhaseId;
import com.jne.model.LastBillingDataSinglePhase;
import com.jne.model.LastBillingDataSinglePhaseId;
import com.jne.model.LastBillingDataThreePhase;
import com.jne.model.LastBillingDataThreePhaseId;
import com.jne.repo.BillingDataSinglePhaseRepo;
import com.jne.repo.BillingDataThreePhaseRepo;
import com.jne.repo.LastBillingDataSinglePhaseRepo;
import com.jne.repo.LastBillingDataThreePhaseRepo;
import com.jne.service.utils.KafkaCommandMetrics;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class BillingDataReadConsumer {

    private static final Logger log = LoggerFactory.getLogger(BillingDataReadConsumer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ThreadLocal<SimpleDateFormat> DF =
        ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            return sdf;
        });

    // --- constants (same tune as cass consumer) ---
    private static final int MAX_PENDING_INSERTS = 20000;
    private static final int RESUME_THRESHOLD    = 10000;
    private static final int MAX_QUEUE_SIZE      = 25000;

    @Value("${kafka.billing.group-id}")
    private String groupId;

    @Value("${feature.queue-writer.enabled:true}")
    private boolean queueWriterEnabled;

    @Value("${feature.queue.batch-size:1000}")
    private int QUEUE_BATCH;

    @Value("${feature.queue.flush-ms:200}")
    private long QUEUE_FLUSH_MS;

    @Value("${feature.queue.concurrency:4}")
    private int QUEUE_CONCURRENCY;

    // ✅ for Postgres: how many saveAll in parallel (threads)
   // private static final AtomicInteger PG_IN_FLIGHT = new AtomicInteger(0);

     private final KafkaCommandMetrics commandMetrics;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    public BillingDataReadConsumer(
    	
            KafkaCommandMetrics commandMetrics,
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry
    ) {
    
        this.commandMetrics = commandMetrics;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    // 4 queues — same as your cass consumer
    private final BlockingQueue<BillingDataSinglePhase> spQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final BlockingQueue<BillingDataThreePhase> tpQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final BlockingQueue<LastBillingDataSinglePhase> spHistoryQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private final BlockingQueue<LastBillingDataThreePhase> tpHistoryQueue =
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    
    private final ScheduledExecutorService drainScheduler =
            Executors.newSingleThreadScheduledExecutor();
    
    @Autowired
    private BillingDataSinglePhaseRepo spRepo;

    @Autowired
    private BillingDataThreePhaseRepo tpRepo;

    @Autowired
    private LastBillingDataSinglePhaseRepo spHistoryRepo;

    @Autowired
    private LastBillingDataThreePhaseRepo tpHistoryRepo;
    
    private final AtomicInteger pendingInserts = new AtomicInteger(0);
    private final AtomicBoolean paused         = new AtomicBoolean(false);

    private static final String SP_LATEST  = "BillingData-1P-Latest-PG";
    private static final String SP_HISTORY = "BillingData-1P-History-PG";
    private static final String TP_LATEST  = "BillingData-3P-Latest-PG";
    private static final String TP_HISTORY = "BillingData-3P-History-PG";


    // ---------------- KAFKA LISTENER ----------------
    @KafkaListener(
        id = "BillingDataListenerPg",
        topics = "BillingData",
        groupId = "${kafka.billing.group-id}",
        concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {
        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        try {
            for (String msg : messages) {
                if (msg == null || msg.isBlank()) continue;
                JsonNode root = MAPPER.readTree(msg);
                parseAndEnqueue(root);
            }

            // ✅ ack after enqueue
            ack.acknowledge();

        } catch (Exception e) {
            log.error("❌ BillingData(Postgres) parse error, not acknowledging", e);
        }
    }
    
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
            List<BillingDataSinglePhase> spBatch = new ArrayList<>(QUEUE_BATCH);
            spQueue.drainTo(spBatch, QUEUE_BATCH);

            List<BillingDataThreePhase> tpBatch = new ArrayList<>(QUEUE_BATCH);
            tpQueue.drainTo(tpBatch, QUEUE_BATCH);

            List<LastBillingDataSinglePhase> spHistoryBatch = new ArrayList<>(QUEUE_BATCH);
            spHistoryQueue.drainTo(spHistoryBatch, QUEUE_BATCH);

            List<LastBillingDataThreePhase> tpHistoryBatch = new ArrayList<>(QUEUE_BATCH);
            tpHistoryQueue.drainTo(tpHistoryBatch, QUEUE_BATCH);

            if (!spBatch.isEmpty()) insertSPBatch(spBatch);
            if (!tpBatch.isEmpty()) insertTPBatch(tpBatch);
            if (!spHistoryBatch.isEmpty()) insertSPHistoryBatch(spHistoryBatch);
            if (!tpHistoryBatch.isEmpty()) insertTPHistoryBatch(tpHistoryBatch);

        } catch (Exception e) {
            log.error("❌ Drain error", e);
        }
    }

    // parse 1 json -> create entities -> enqueue
    private void parseAndEnqueue(JsonNode root) {
        try {
            final String meterType  = root.path("meterType").asText(null);
            final String meterNo    = root.path("meterNo").asText(null);
            final String systemTime = root.path("systemTime").asText(null);

            final Date mdasDateTime =
                (systemTime == null || systemTime.isBlank())
                    ? new Date()
                    : safeParseDate(systemTime);

            JsonNode dataNode = root.path("data");
            if (!dataNode.isArray() || dataNode.size() == 0) return;
            JsonNode obisData = dataNode.get(0);

            JsonNode codesNode = obisData.path("1");
            if (!codesNode.isArray() || codesNode.size() == 0) return;

            List<String> obisCodes = MAPPER.convertValue(
                codesNode, new TypeReference<List<String>>() {}
            );

            // keys except "1"
            List<String> blockKeys = new ArrayList<>();
            obisData.fieldNames().forEachRemaining(blockKeys::add);
            blockKeys.remove("1");
            if (blockKeys.isEmpty()) return;
           // String lastKey = blockKeys.get(blockKeys.size() - 1);
            
            Map<String, Date> blockDateMap = new HashMap<>();

            for (String block : blockKeys) {
                JsonNode valuesNode = obisData.path(block);
                try {
                    JsonNode first = valuesNode.get(0);
                    if (first != null && !first.isNull()) {
                        Date dt = safeParseDate(first.asText());
                        blockDateMap.put(block, dt);
                    }
                } catch (Exception ignored) {}
            }

            String latestKey = blockDateMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            for (String blockKey : blockKeys) {
                JsonNode valuesNode = obisData.path(blockKey);
                List<Object> obisValues = MAPPER.convertValue(
                    valuesNode, new TypeReference<List<Object>>() {}
                );

                // billing datetime (first value)
                Date billingTs = extractBillingTs(valuesNode, mdasDateTime, meterNo);

                boolean isLatest = blockKey.equals(latestKey);
                if ("Single Phase".equalsIgnoreCase(meterType)) {

                    if (isLatest) {
                        BillingDataSinglePhase e = new BillingDataSinglePhase();
                        // ✅ you said your JPA entity uses EmbeddedId, so set id properly:
                        BillingDataSinglePhaseId id = new BillingDataSinglePhaseId();
                        id.setDeviceSerialNumber(meterNo);
                        id.setBillingDatetime(toLocalDateTime(billingTs));
                        e.setId(id);

                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
                        
                        e.setReadingType(1);

                        fillFields(obisCodes, obisValues, e, BillingDataSinglePhase.class, meterType);
                      //  enqueue(spLatestQueue, e, "BillingData-1P-Latest-PG");
                        enqueue(spQueue, e, "BillingData-1P-Latest-PG");

                    } else {
                        LastBillingDataSinglePhase e = new LastBillingDataSinglePhase();
                        LastBillingDataSinglePhaseId id = new LastBillingDataSinglePhaseId();
                        id.setDeviceSerialNumber(meterNo);
                        id.setBillingDatetime(toLocalDateTime(billingTs));
                        e.setId(id);

                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
                        e.setReadingType(1);


                        fillFields(obisCodes, obisValues, e, LastBillingDataSinglePhase.class, meterType);
                        enqueue(spHistoryQueue, e, "BillingData-1P-History-PG");
                    }

                } else if (meterType != null &&
                        (meterType.equalsIgnoreCase("Three Phase")
                         || meterType.equalsIgnoreCase("CT Meter")
                         || meterType.equalsIgnoreCase("HT Meter"))) {

                    if (isLatest) {
                        BillingDataThreePhase e = new BillingDataThreePhase();
                        BillingDataThreePhaseId id = new BillingDataThreePhaseId();
                        id.setDeviceSerialNumber(meterNo);
                        id.setBillingDatetime(toLocalDateTime(billingTs));
                        e.setId(id);

                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
                        e.setReadingType(1);


                        fillFields(obisCodes, obisValues, e, BillingDataThreePhase.class, meterType);
                        enqueue(tpQueue, e, "BillingData-3P-Latest-PG");

                    } else {
                        LastBillingDataThreePhase e = new LastBillingDataThreePhase();
                        LastBillingDataThreePhaseId id = new LastBillingDataThreePhaseId();
                        id.setDeviceSerialNumber(meterNo);
                        id.setBillingDatetime(toLocalDateTime(billingTs));
                        e.setId(id);

                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
                        e.setReadingType(1);


                        fillFields(obisCodes, obisValues, e, LastBillingDataThreePhase.class, meterType);
                        enqueue(tpHistoryQueue, e, "BillingData-3P-History-PG");
                    }

                } else {
                    log.warn("⚠ BillingData(Postgres) unknown meterType={}", meterType);
                }
            }

        } catch (Exception e) {
            log.error("❌ BillingData(Postgres) parse failed: {}", e.getMessage(), e);
        }
    }

    private Date safeParseDate(String s) {
        try {
            String cleaned = s.trim();
            return DF.get().parse(cleaned);
        } catch (Exception ex) {
            return new Date();
        }
    }

    private Date extractBillingTs(JsonNode valuesNode, Date mdasDateTime, String meterNo) {
        Date billingTs = null;
        try {
            JsonNode first = valuesNode.get(0);
            if (first != null && !first.isNull()) {
                String ts = first.asText().trim();
                if (!ts.isEmpty() && !"null".equalsIgnoreCase(ts)) {
                    billingTs = safeParseDate(ts);
                }
            }
        } catch (Exception e) {
            log.warn("⚠ Billing(PG) invalid billing_datetime meter {}: {}", meterNo, e.getMessage());
        }
        return (billingTs != null) ? billingTs : mdasDateTime;
    }

    private LocalDateTime toLocalDateTime(Date d) {
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    // enqueue (same style)
    private <T> void enqueue(BlockingQueue<T> q, T e, String metricKey) {
        if (!q.offer(e)) {
            log.warn("🧯 Queue full, pausing Kafka");
            forcePause();
        }
        pendingInserts.incrementAndGet();
        commandMetrics.incrementConsumed(metricKey, 1);
    }

    private int getTotalQueueSize() {
        return spQueue.size()
             + tpQueue.size()
             + spHistoryQueue.size()
             + tpHistoryQueue.size();
    }



    // ---- PG persist (saveAll) ----
//    private void waitIfInflightHigh() throws InterruptedException {
//        while (PG_IN_FLIGHT.get() >= PG_MAX_IN_FLIGHT) {
//            Thread.sleep(10);
//        }
//    }



    // -------------- pause / resume --------------
    @Scheduled(fixedDelay = 1000)
    public void monitorAndThrottle() {

        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("BillingDataListenerPg");

        if (c == null) return;

        int totalBacklog = getTotalQueueSize();

        if (totalBacklog > MAX_PENDING_INSERTS && paused.compareAndSet(false, true)) {
            log.warn("⏸ Pausing Kafka (backlog={})", totalBacklog);
            c.pause();
            return;
        }

        if (totalBacklog < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
            log.info("▶ Resuming Kafka (backlog={})", totalBacklog);
            c.resume();
        }
    }

    private void forcePause() {
        MessageListenerContainer c =
            kafkaListenerEndpointRegistry.getListenerContainer("BillingDataListenerPg");
        if (c != null && paused.compareAndSet(false, true)) {
            log.warn("⏸ Force-pausing BillingDataListenerPg");
            c.pause();
        }
    }

    // -------------- mapping (same as yours) --------------
    private void fillFields(
        List<String> obisCodes,
        List<Object> obisValues,
        Object entity,
        Class<?> clazz,
        String meterType
    ) {
        Map<String, Map<Integer, String>> indexMap =
            "Single Phase".equalsIgnoreCase(meterType)
                ? ObisFieldMapping.getObisIndexMapping()
                : ObisFieldMapping.getObisIndexMapping3p();

        Map<String, Integer> seen = new HashMap<>();
        int n = Math.min(obisCodes.size(), obisValues.size());

        for (int i = 0; i < n; i++) {
            String obis = obisCodes.get(i);
            Object value = obisValues.get(i);
            if (value == null || "null".equalsIgnoreCase(String.valueOf(value))) continue;

            int dup = seen.getOrDefault(obis, 0);
            String fieldName;

            if (indexMap != null && indexMap.containsKey(obis)) {
                fieldName = indexMap.get(obis).get(dup);
            } else {
                fieldName =
                    "Single Phase".equalsIgnoreCase(meterType)
                        ? ObisFieldMapping.getBillingFieldMappingSinglePhase().get(obis)
                        : ObisFieldMapping.getBillingFieldMappingThreePhase().get(obis);
            }

            if (fieldName != null) {
                setField(entity, clazz, fieldName, value);
            }

            seen.put(obis, dup + 1);
        }
    }
    
    private void insertSPBatch(List<BillingDataSinglePhase> batch) {

        try {
            // 🚀 FAST BATCH
            spRepo.saveAll(batch);

            commandMetrics.incrementSaved(SP_LATEST, batch.size());

        } catch (DataIntegrityViolationException e) {

            log.warn("⚠ SP batch failed → fallback to row insert");

            int success = 0;

            for (BillingDataSinglePhase entity : batch) {
                try {
                    spRepo.save(entity);
                    success++;
                } catch (DataIntegrityViolationException ignore) {
                    // ✅ duplicate → ignore but count as processed
                    success++;
                } catch (Exception ex) {
                    log.error("❌ SP row insert failed: {}", ex.getMessage());
                }
            }

            commandMetrics.incrementSaved(SP_LATEST, success);

        } catch (Exception e) {

            log.error("❌ SP insert failed completely", e);

            // 🔁 requeue
            batch.forEach(spQueue::offer);
        }

        pendingInserts.addAndGet(-batch.size());
    }

    private void insertTPBatch(List<BillingDataThreePhase> batch) {

        try {
            tpRepo.saveAll(batch);

            commandMetrics.incrementSaved(TP_LATEST, batch.size());

        } catch (DataIntegrityViolationException e) {

            log.warn("⚠ TP batch failed → fallback to row insert");

            int success = 0;

            for (BillingDataThreePhase entity : batch) {
                try {
                    tpRepo.save(entity);
                    success++;
                } catch (DataIntegrityViolationException ignore) {
                    success++;
                } catch (Exception ex) {
                    log.error("❌ TP row insert failed: {}", ex.getMessage());
                }
            }

            commandMetrics.incrementSaved(TP_LATEST, success);

        } catch (Exception e) {

            log.error("❌ TP insert failed completely", e);
            batch.forEach(tpQueue::offer);
        }

        pendingInserts.addAndGet(-batch.size());
    }

    private void insertSPHistoryBatch(List<LastBillingDataSinglePhase> batch) {

        try {
            spHistoryRepo.saveAll(batch);

            commandMetrics.incrementSaved(SP_HISTORY, batch.size());

        } catch (DataIntegrityViolationException e) {

            log.warn("⚠ SP History batch failed → fallback");

            int success = 0;

            for (LastBillingDataSinglePhase entity : batch) {
                try {
                    spHistoryRepo.save(entity);
                    success++;
                } catch (DataIntegrityViolationException ignore) {
                    success++;
                } catch (Exception ex) {
                    log.error("❌ SP History row insert failed: {}", ex.getMessage());
                }
            }

            commandMetrics.incrementSaved(SP_HISTORY, success);

        } catch (Exception e) {

            log.error("❌ SP History insert failed", e);
            batch.forEach(spHistoryQueue::offer);
        }

        pendingInserts.addAndGet(-batch.size());
    }

    private void insertTPHistoryBatch(List<LastBillingDataThreePhase> batch) {

        try {
            tpHistoryRepo.saveAll(batch);

            commandMetrics.incrementSaved(TP_HISTORY, batch.size());

        } catch (DataIntegrityViolationException e) {

            log.warn("⚠ TP History batch failed → fallback");

            int success = 0;

            for (LastBillingDataThreePhase entity : batch) {
                try {
                    tpHistoryRepo.save(entity);
                    success++;
                } catch (DataIntegrityViolationException ignore) {
                    success++;
                } catch (Exception ex) {
                    log.error("❌ TP History row insert failed: {}", ex.getMessage());
                }
            }

            commandMetrics.incrementSaved(TP_HISTORY, success);

        } catch (Exception e) {

            log.error("❌ TP History insert failed", e);
            batch.forEach(tpHistoryQueue::offer);
        }

        pendingInserts.addAndGet(-batch.size());
    }

    private String toCamelCase(String s) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;

        for (char c : s.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                result.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return result.toString();
    }

    private void setField(Object entity, Class<?> clazz, String fieldName, Object value) {
        try {

            Field f = null;
            Class<?> current = clazz;

            while (current != null) {
                try {
                    // 🔥 try original
                    f = current.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    try {
                        // 🔥 try camelCase
                        String camel = toCamelCase(fieldName);
                        f = current.getDeclaredField(camel);
                        fieldName = camel; // update name
                        break;
                    } catch (NoSuchFieldException ex) {
                        current = current.getSuperclass();
                    }
                }
            }

            if (f == null) {
                log.warn("❌ Field NOT FOUND: {}", fieldName);
                return;
            }

            f.setAccessible(true);

            if (value == null) return;

            String s = String.valueOf(value).trim();
            if (s.isEmpty() || "null".equalsIgnoreCase(s)) return;

            Class<?> type = f.getType();

            // ✅ LocalDateTime
            if (type == LocalDateTime.class) {
                try {
                    Date d = DF.get().parse(s);
                    f.set(entity, toLocalDateTime(d));
                } catch (Exception e) {
                    log.warn("❌ Date parse failed for {} value {}", fieldName, s);
                }
                return;
            }

            // ✅ Double
            if (type == Double.class || type == double.class) {
                f.set(entity, Double.parseDouble(s));
                return;
            }

            // ✅ Integer
            if (type == Integer.class || type == int.class) {
                f.set(entity, (int) Double.parseDouble(s));
                return;
            }

            // ✅ Long
            if (type == Long.class || type == long.class) {
                f.set(entity, (long) Double.parseDouble(s));
                return;
            }

            // ✅ String
            if (type == String.class) {
                f.set(entity, s);
                return;
            }

        } catch (Exception e) {
            log.error("❌ setField failed for {}: {}", fieldName, e.getMessage());
        }
    }
    
//    private Map<String, Object> toColumnMap(Object entity) throws IllegalAccessException {
//
//        Map<String, Object> map = new LinkedHashMap<>();
//
//        Class<?> clazz = entity.getClass();
//
//        while (clazz != null) {
//
//            for (Field field : clazz.getDeclaredFields()) {
//                field.setAccessible(true);
//
//                Object value = field.get(entity);
//                if (value == null) continue;
//
//                // ✅ handle EmbeddedId
//                if (field.isAnnotationPresent(EmbeddedId.class)) {
//
//                    Object idObj = value;
//
//                    for (Field idField : idObj.getClass().getDeclaredFields()) {
//                        idField.setAccessible(true);
//
//                        Object idVal = idField.get(idObj);
//                        if (idVal == null) continue;
//
//                        Column col = idField.getAnnotation(Column.class);
//
//                        String colName = (col != null)
//                                ? col.name()
//                                : idField.getName();
//
//                        map.put(colName, idVal);
//                    }
//
//                    continue;
//                }
//
//                // ✅ use @Column name
//                Column column = field.getAnnotation(Column.class);
//
//                String colName = (column != null && !column.name().isEmpty())
//                        ? column.name()
//                        : field.getName();
//
//                map.put(colName, value);
//            }
//
//            clazz = clazz.getSuperclass();
//        }
//
//        return map;
//    }

    
//    private <T> void batchInsert(String tableName, List<T> batch) {
//
//        if (batch == null || batch.isEmpty()) return;
//
//        try {
//
//            Map<String, Object> sampleMap = toColumnMap(batch.get(0));
//            List<String> columns = new ArrayList<>(sampleMap.keySet());
//
//            String columnCsv = String.join(", ", columns);
//            String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));
//
//            String sql = "INSERT INTO " + tableName +
//                    " (" + columnCsv + ") VALUES (" + placeholders + ") " +
//                    "ON CONFLICT (day, device_serial_number, billing_datetime) DO NOTHING";
//
//            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, entity) -> {
//
//                try {
//                    Map<String, Object> map = toColumnMap(entity);
//
//                    for (int i = 0; i < columns.size(); i++) {
//                        ps.setObject(i + 1, map.get(columns.get(i)));
//                    }
//
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//        } catch (Exception e) {
//            throw new RuntimeException("Batch insert failed", e);
//        }
//    }

}





//package com.jne.utils;
//
//
//
//import java.lang.reflect.Field;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.kafka.listener.MessageListenerContainer;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jne.model.BillingDataSinglePhase;
//import com.jne.model.BillingDataSinglePhaseId;
//import com.jne.model.BillingDataThreePhase;
//import com.jne.model.BillingDataThreePhaseId;
//import com.jne.model.LastBillingDataSinglePhase;
//import com.jne.model.LastBillingDataSinglePhaseId;
//import com.jne.model.LastBillingDataThreePhase;
//import com.jne.model.LastBillingDataThreePhaseId;
//import com.jne.service.utils.KafkaCommandMetrics;
//import jakarta.annotation.PostConstruct;
//
//@Component
//@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
//@EnableScheduling
//public class BillingDataReadConsumer {
//
//    private static final Logger log = LoggerFactory.getLogger(BillingDataReadConsumer.class);
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static final ThreadLocal<SimpleDateFormat> DF =
//        ThreadLocal.withInitial(() -> {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            sdf.setLenient(false);
//            return sdf;
//        });
//
//    // --- constants (same tune as cass consumer) ---
//    private static final int MAX_PENDING_INSERTS = 20000;
//    private static final int RESUME_THRESHOLD    = 10000;
//    private static final int MAX_QUEUE_SIZE      = 50000;
//
//    @Value("${kafka.billing.group-id}")
//    private String groupId;
//
//    @Value("${feature.queue-writer.enabled:true}")
//    private boolean queueWriterEnabled;
//
//    @Value("${feature.queue.batch-size:1000}")
//    private int QUEUE_BATCH;
//
//    @Value("${feature.queue.flush-ms:200}")
//    private long QUEUE_FLUSH_MS;
//
//    @Value("${feature.queue.concurrency:4}")
//    private int QUEUE_CONCURRENCY;
//
//    // ✅ for Postgres: how many saveAll in parallel (threads)
//   // private static final AtomicInteger PG_IN_FLIGHT = new AtomicInteger(0);
//
//     private final KafkaCommandMetrics commandMetrics;
//    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//    
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public BillingDataReadConsumer(
//    	
//            KafkaCommandMetrics commandMetrics,
//            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry
//    ) {
//    
//        this.commandMetrics = commandMetrics;
//        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
//    }
//
//    // 4 queues — same as your cass consumer
//    private final BlockingQueue<BillingDataSinglePhase> spQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//
//    private final BlockingQueue<BillingDataThreePhase> tpQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//
//    private final BlockingQueue<LastBillingDataSinglePhase> spHistoryQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//
//    private final BlockingQueue<LastBillingDataThreePhase> tpHistoryQueue =
//            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
//    
//    private final ScheduledExecutorService drainScheduler =
//            Executors.newSingleThreadScheduledExecutor();
//    
//    
//    private final AtomicInteger pendingInserts = new AtomicInteger(0);
//    private final AtomicBoolean paused         = new AtomicBoolean(false);
//
//    private static final String SP_LATEST  = "BillingData-1P-Latest-PG";
//    private static final String SP_HISTORY = "BillingData-1P-History-PG";
//    private static final String TP_LATEST  = "BillingData-3P-Latest-PG";
//    private static final String TP_HISTORY = "BillingData-3P-History-PG";
//
//
//    // ---------------- KAFKA LISTENER ----------------
//    @KafkaListener(
//        id = "BillingDataListenerPg",
//        topics = "BillingData",
//        groupId = "${kafka.billing.group-id}",
//        concurrency = "${feature.queue.concurrency:6}"
//    )
//    public void consume(List<String> messages, Acknowledgment ack) {
//        if (messages == null || messages.isEmpty()) {
//            ack.acknowledge();
//            return;
//        }
//
//        try {
//            for (String msg : messages) {
//                if (msg == null || msg.isBlank()) continue;
//                JsonNode root = MAPPER.readTree(msg);
//                parseAndEnqueue(root);
//            }
//
//            // ✅ ack after enqueue
//            ack.acknowledge();
//
//        } catch (Exception e) {
//            log.error("❌ BillingData(Postgres) parse error, not acknowledging", e);
//        }
//    }
//    
//    @PostConstruct
//    public void startDrainLoop() {
//        drainScheduler.scheduleWithFixedDelay(
//            this::drainAndSave,
//            0,
//            200,
//            TimeUnit.MILLISECONDS
//        );
//    }
//    private void drainAndSave() {
//        try {
//            List<BillingDataSinglePhase> spBatch = new ArrayList<>(QUEUE_BATCH);
//            spQueue.drainTo(spBatch, QUEUE_BATCH);
//
//            List<BillingDataThreePhase> tpBatch = new ArrayList<>(QUEUE_BATCH);
//            tpQueue.drainTo(tpBatch, QUEUE_BATCH);
//
//            List<LastBillingDataSinglePhase> spHistoryBatch = new ArrayList<>(QUEUE_BATCH);
//            spHistoryQueue.drainTo(spHistoryBatch, QUEUE_BATCH);
//
//            List<LastBillingDataThreePhase> tpHistoryBatch = new ArrayList<>(QUEUE_BATCH);
//            tpHistoryQueue.drainTo(tpHistoryBatch, QUEUE_BATCH);
//
//            if (!spBatch.isEmpty()) insertSPBatch(spBatch);
//            if (!tpBatch.isEmpty()) insertTPBatch(tpBatch);
//            if (!spHistoryBatch.isEmpty()) insertSPHistoryBatch(spHistoryBatch);
//            if (!tpHistoryBatch.isEmpty()) insertTPHistoryBatch(tpHistoryBatch);
//
//        } catch (Exception e) {
//            log.error("❌ Drain error", e);
//        }
//    }
//
//    // parse 1 json -> create entities -> enqueue
//    private void parseAndEnqueue(JsonNode root) {
//        try {
//            final String meterType  = root.path("meterType").asText(null);
//            final String meterNo    = root.path("meterNo").asText(null);
//            final String systemTime = root.path("systemTime").asText(null);
//
//            final Date mdasDateTime =
//                (systemTime == null || systemTime.isBlank())
//                    ? new Date()
//                    : safeParseDate(systemTime);
//
//            JsonNode dataNode = root.path("data");
//            if (!dataNode.isArray() || dataNode.size() == 0) return;
//            JsonNode obisData = dataNode.get(0);
//
//            JsonNode codesNode = obisData.path("1");
//            if (!codesNode.isArray() || codesNode.size() == 0) return;
//
//            List<String> obisCodes = MAPPER.convertValue(
//                codesNode, new TypeReference<List<String>>() {}
//            );
//
//            // keys except "1"
//            List<String> blockKeys = new ArrayList<>();
//            obisData.fieldNames().forEachRemaining(blockKeys::add);
//            blockKeys.remove("1");
//            if (blockKeys.isEmpty()) return;
//           // String lastKey = blockKeys.get(blockKeys.size() - 1);
//            
//            Map<String, Date> blockDateMap = new HashMap<>();
//
//            for (String block : blockKeys) {
//                JsonNode valuesNode = obisData.path(block);
//                try {
//                    JsonNode first = valuesNode.get(0);
//                    if (first != null && !first.isNull()) {
//                        Date dt = safeParseDate(first.asText());
//                        blockDateMap.put(block, dt);
//                    }
//                } catch (Exception ignored) {}
//            }
//
//            String latestKey = blockDateMap.entrySet()
//                    .stream()
//                    .max(Map.Entry.comparingByValue())
//                    .map(Map.Entry::getKey)
//                    .orElse(null);
//
//            for (String blockKey : blockKeys) {
//                JsonNode valuesNode = obisData.path(blockKey);
//                List<Object> obisValues = MAPPER.convertValue(
//                    valuesNode, new TypeReference<List<Object>>() {}
//                );
//
//                // billing datetime (first value)
//                Date billingTs = extractBillingTs(valuesNode, mdasDateTime, meterNo);
//
//                LocalDate day = billingTs.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                boolean isLatest = blockKey.equals(latestKey);
//                if ("Single Phase".equalsIgnoreCase(meterType)) {
//
//                    if (isLatest) {
//                        BillingDataSinglePhase e = new BillingDataSinglePhase();
//                        // ✅ you said your JPA entity uses EmbeddedId, so set id properly:
//                        BillingDataSinglePhaseId id = new BillingDataSinglePhaseId();
//                        id.setDeviceSerialNumber(meterNo);
//                        id.setDay(day);
//                        id.setBillingDatetime(toLocalDateTime(billingTs));
//                        e.setId(id);
//
//                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
//
//                        fillFields(obisCodes, obisValues, e, BillingDataSinglePhase.class, meterType);
//                      //  enqueue(spLatestQueue, e, "BillingData-1P-Latest-PG");
//                        enqueue(spQueue, e, "BillingData-1P-Latest-PG");
//
//                    } else {
//                        LastBillingDataSinglePhase e = new LastBillingDataSinglePhase();
//                        LastBillingDataSinglePhaseId id = new LastBillingDataSinglePhaseId();
//                        id.setDeviceSerialNumber(meterNo);
//                        id.setDay(day);
//                        id.setBillingDatetime(toLocalDateTime(billingTs));
//                        e.setId(id);
//
//                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
//
//                        fillFields(obisCodes, obisValues, e, LastBillingDataSinglePhase.class, meterType);
//                        enqueue(spHistoryQueue, e, "BillingData-1P-History-PG");
//                    }
//
//                } else if (meterType != null &&
//                        (meterType.equalsIgnoreCase("Three Phase")
//                         || meterType.equalsIgnoreCase("CT Meter")
//                         || meterType.equalsIgnoreCase("HT Meter"))) {
//
//                    if (isLatest) {
//                        BillingDataThreePhase e = new BillingDataThreePhase();
//                        BillingDataThreePhaseId id = new BillingDataThreePhaseId();
//                        id.setDeviceSerialNumber(meterNo);
//                        id.setDay(day);
//                        id.setBillingDatetime(toLocalDateTime(billingTs));
//                        e.setId(id);
//
//                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
//
//                        fillFields(obisCodes, obisValues, e, BillingDataThreePhase.class, meterType);
//                        enqueue(tpQueue, e, "BillingData-3P-Latest-PG");
//
//                    } else {
//                        LastBillingDataThreePhase e = new LastBillingDataThreePhase();
//                        LastBillingDataThreePhaseId id = new LastBillingDataThreePhaseId();
//                        id.setDeviceSerialNumber(meterNo);
//                        id.setDay(day);
//                        id.setBillingDatetime(toLocalDateTime(billingTs));
//                        e.setId(id);
//
//                        e.setMdasDatetime(toLocalDateTime(mdasDateTime));
//
//                        fillFields(obisCodes, obisValues, e, LastBillingDataThreePhase.class, meterType);
//                        enqueue(tpHistoryQueue, e, "BillingData-3P-History-PG");
//                    }
//
//                } else {
//                    log.warn("⚠ BillingData(Postgres) unknown meterType={}", meterType);
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("❌ BillingData(Postgres) parse failed: {}", e.getMessage(), e);
//        }
//    }
//
//    private Date safeParseDate(String s) {
//        try {
//            String cleaned = s.trim();
//            return DF.get().parse(cleaned);
//        } catch (Exception ex) {
//            return new Date();
//        }
//    }
//
//    private Date extractBillingTs(JsonNode valuesNode, Date mdasDateTime, String meterNo) {
//        Date billingTs = null;
//        try {
//            JsonNode first = valuesNode.get(0);
//            if (first != null && !first.isNull()) {
//                String ts = first.asText().trim();
//                if (!ts.isEmpty() && !"null".equalsIgnoreCase(ts)) {
//                    billingTs = safeParseDate(ts);
//                }
//            }
//        } catch (Exception e) {
//            log.warn("⚠ Billing(PG) invalid billing_datetime meter {}: {}", meterNo, e.getMessage());
//        }
//        return (billingTs != null) ? billingTs : mdasDateTime;
//    }
//
//    private LocalDateTime toLocalDateTime(Date d) {
//        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
//    }
//
//    // enqueue (same style)
//    private <T> void enqueue(BlockingQueue<T> q, T e, String metricKey) {
//        if (!q.offer(e)) {
//            log.warn("🧯 Queue full, pausing Kafka");
//            forcePause();
//        }
//        pendingInserts.incrementAndGet();
//        commandMetrics.incrementConsumed(metricKey, 1);
//    }
//
//    private int getTotalQueueSize() {
//        return spQueue.size()
//             + tpQueue.size()
//             + spHistoryQueue.size()
//             + tpHistoryQueue.size();
//    }
//
//
//
//    // ---- PG persist (saveAll) ----
////    private void waitIfInflightHigh() throws InterruptedException {
////        while (PG_IN_FLIGHT.get() >= PG_MAX_IN_FLIGHT) {
////            Thread.sleep(10);
////        }
////    }
//
//
//
//    // -------------- pause / resume --------------
//    @Scheduled(fixedDelay = 1000)
//    public void monitorAndThrottle() {
//
//        MessageListenerContainer c =
//            kafkaListenerEndpointRegistry.getListenerContainer("BillingDataListenerPg");
//
//        if (c == null) return;
//
//        int totalBacklog = getTotalQueueSize();
//
//        if (totalBacklog > MAX_PENDING_INSERTS && paused.compareAndSet(false, true)) {
//            log.warn("⏸ Pausing Kafka (backlog={})", totalBacklog);
//            c.pause();
//            return;
//        }
//
//        if (totalBacklog < RESUME_THRESHOLD && paused.compareAndSet(true, false)) {
//            log.info("▶ Resuming Kafka (backlog={})", totalBacklog);
//            c.resume();
//        }
//    }
//
//    private void forcePause() {
//        MessageListenerContainer c =
//            kafkaListenerEndpointRegistry.getListenerContainer("BillingDataListenerPg");
//        if (c != null && paused.compareAndSet(false, true)) {
//            log.warn("⏸ Force-pausing BillingDataListenerPg");
//            c.pause();
//        }
//    }
//
//    // -------------- mapping (same as yours) --------------
//    private void fillFields(
//        List<String> obisCodes,
//        List<Object> obisValues,
//        Object entity,
//        Class<?> clazz,
//        String meterType
//    ) {
//        Map<String, Map<Integer, String>> indexMap =
//            "Single Phase".equalsIgnoreCase(meterType)
//                ? ObisFieldMapping.getObisIndexMapping()
//                : ObisFieldMapping.getObisIndexMapping3p();
//
//        Map<String, Integer> seen = new HashMap<>();
//        int n = Math.min(obisCodes.size(), obisValues.size());
//
//        for (int i = 0; i < n; i++) {
//            String obis = obisCodes.get(i);
//            Object value = obisValues.get(i);
//            if (value == null || "null".equalsIgnoreCase(String.valueOf(value))) continue;
//
//            int dup = seen.getOrDefault(obis, 0);
//            String fieldName;
//
//            if (indexMap != null && indexMap.containsKey(obis)) {
//                fieldName = indexMap.get(obis).get(dup);
//            } else {
//                fieldName =
//                    "Single Phase".equalsIgnoreCase(meterType)
//                        ? ObisFieldMapping.getBillingFieldMappingSinglePhase().get(obis)
//                        : ObisFieldMapping.getBillingFieldMappingThreePhase().get(obis);
//            }
//
//            if (fieldName != null) {
//                setField(entity, clazz, fieldName, value);
//            }
//
//            seen.put(obis, dup + 1);
//        }
//    }
//    
//    private void insertSPBatch(List<BillingDataSinglePhase> batch) {
//
//        String sql = "INSERT INTO billing_data_singlephase " +
//                "(device_serial_number, day, billing_datetime, mdas_datetime) " +
//                "VALUES (?, ?, ?, ?) " +
//                "ON CONFLICT (day, device_serial_number, billing_datetime) DO NOTHING";
//
//
//        try {
//            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, e) -> {
//                ps.setString(1, e.getId().getDeviceSerialNumber());
//                ps.setObject(2, e.getId().getDay());
//                ps.setObject(3, e.getId().getBillingDatetime());
//                ps.setObject(4, e.getMdasDatetime());
//            });
//
//            // ✅ ADD THIS
//            commandMetrics.incrementSaved(SP_LATEST , batch.size()); // ✅ FIX
//        } catch (Exception e) {
//            log.error("❌ SP insert failed", e);
//            batch.forEach(spQueue::offer); // retry
//        }
//
//        pendingInserts.addAndGet(-batch.size());
//    }
//    
//    private void insertTPBatch(List<BillingDataThreePhase> batch) {
//
//        String sql = "INSERT INTO billing_data_threephase " +
//                "(device_serial_number, day, billing_datetime, mdas_datetime) " +
//                "VALUES (?, ?, ?, ?) " +
//                "ON CONFLICT (day, device_serial_number, billing_datetime) DO NOTHING";
//
//        try {
//        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, e) -> {
//            ps.setString(1, e.getId().getDeviceSerialNumber());
//            ps.setObject(2, e.getId().getDay());
//            ps.setObject(3, e.getId().getBillingDatetime());
//            ps.setObject(4, e.getMdasDatetime());
//        });
//        
//        commandMetrics.incrementSaved(TP_LATEST  , batch.size());
//        } catch (Exception e) {
//            log.error("❌ SP insert failed", e);
//            batch.forEach(tpQueue::offer); // retry
//        }
//
//        pendingInserts.addAndGet(-batch.size());
//    }
//    
//    private void insertSPHistoryBatch(List<LastBillingDataSinglePhase> batch) {
//
//        String sql = "INSERT INTO last_billing_data_singlephase " +
//                "(device_serial_number, day, billing_datetime, mdas_datetime) " +
//                "VALUES (?, ?, ?, ?) " +
//                "ON CONFLICT (day, device_serial_number, billing_datetime) DO NOTHING";
//
//        try {
//        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, e) -> {
//            ps.setString(1, e.getId().getDeviceSerialNumber());
//            ps.setObject(2, e.getId().getDay());
//            ps.setObject(3, e.getId().getBillingDatetime());
//            ps.setObject(4, e.getMdasDatetime());
//        });
//        
//        commandMetrics.incrementSaved(SP_HISTORY , batch.size()); // ✅ FIX
//
//        } catch (Exception e) {
//            log.error("❌ SP insert failed", e);
//            batch.forEach(spHistoryQueue::offer); // retry
//        }
//
//        pendingInserts.addAndGet(-batch.size());
//    }
//    
//    private void insertTPHistoryBatch(List<LastBillingDataThreePhase> batch) {
//
//        String sql = "INSERT INTO last_billing_data_threephase " +
//                "(device_serial_number, day, billing_datetime, mdas_datetime) " +
//                "VALUES (?, ?, ?, ?) " +
//                "ON CONFLICT (day, device_serial_number, billing_datetime) DO NOTHING";
//
//        try {
//        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, e) -> {
//            ps.setString(1, e.getId().getDeviceSerialNumber());
//            ps.setObject(2, e.getId().getDay());
//            ps.setObject(3, e.getId().getBillingDatetime());
//            ps.setObject(4, e.getMdasDatetime());
//        });
//        commandMetrics.incrementSaved(TP_HISTORY , batch.size());
//        } catch (Exception e) {
//            log.error("❌ SP insert failed", e);
//            batch.forEach(tpHistoryQueue::offer); // retry
//        }
//
//
//        pendingInserts.addAndGet(-batch.size());
//    }
//
//    private void setField(Object entity, Class<?> clazz, String fieldName, Object value) {
//        try {
//
//            Field f = null;
//            Class<?> current = clazz;
//
//            // 🔵 search field in class hierarchy
//            while (current != null) {
//                try {
//                    f = current.getDeclaredField(fieldName);
//                    break;
//                } catch (NoSuchFieldException e) {
//                    current = current.getSuperclass();
//                }
//            }
//
//            if (f == null) {
//                log.debug("Field '{}' not found on {}", fieldName, clazz.getSimpleName());
//                return;
//            }
//
//            f.setAccessible(true);
//
//            if (value == null) return;
//
//            String s = String.valueOf(value).trim();
//            if (s.isEmpty() || "null".equalsIgnoreCase(s)) return;
//
//            Class<?> type = f.getType();
//
//            // 🔵 LocalDateTime
//            if (type == LocalDateTime.class) {
//                try {
//                    Date d = DF.get().parse(s);
//                    f.set(entity, toLocalDateTime(d));
//                } catch (Exception ignored) {}
//                return;
//            }
//
//            // 🔵 Date
//            if (type == Date.class) {
//                try {
//                    f.set(entity, DF.get().parse(s));
//                } catch (Exception ignored) {}
//                return;
//            }
//
//            // 🔵 Double
//            if (type == Double.class || type == double.class) {
//                f.set(entity, Double.parseDouble(s));
//                return;
//            }
//
//            // 🔵 Integer
//            if (type == Integer.class || type == int.class) {
//                int iv = s.contains(".") ? (int) Double.parseDouble(s) : Integer.parseInt(s);
//                f.set(entity, iv);
//                return;
//            }
//
//            // 🔵 Long
//            if (type == Long.class || type == long.class) {
//                long lv = s.contains(".") ? (long) Double.parseDouble(s) : Long.parseLong(s);
//                f.set(entity, lv);
//                return;
//            }
//
//            // 🔵 String
//            if (type == String.class) {
//                f.set(entity, s);
//                return;
//            }
//
//        } catch (Exception e) {
//            log.error("❌ PG Billing: setField {} failed: {}", fieldName, e.getMessage(), e);
//        }
//    }
//}