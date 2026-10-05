package com.jne.utils;



import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jne.model.InstantaneousPushSinglePhase;
import com.jne.model.InstantaneousPushThreePhase;
import com.jne.repo.InstantPushDataSinglePhaseRepository;
import com.jne.repo.InstantaneousPushThreePhaseRepository;
import com.jne.service.utils.KafkaCommandMetrics;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableScheduling
public class InstantPushDataConsumer {

    private static final Logger log = LoggerFactory.getLogger(InstantPushDataConsumer.class);

    @Value("${kafka.instantpush.group-id}")
    private String groupId;

    @Autowired
    private InstantaneousPushThreePhaseRepository threePhaseRepo;

    @Autowired
    private InstantPushDataSinglePhaseRepository singlePhaseRepo;

    @Autowired
    private KafkaCommandMetrics commandMetrics;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int WRITE_CONCURRENCY = 16;
    private static final int BUFFER_SIZE = 2000;

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 5000L;

    private static final ThreadLocal<SimpleDateFormat> DF =
        ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            return sdf;
        });

    @KafkaListener(
            id = "InstantPushData",
            topics = "instant-push-data",
            groupId = "${kafka.instantpush.group-id:instant-push-group}",
            concurrency = "${feature.queue.concurrency:6}"
    )
    public void consume(List<String> messages, Acknowledgment ack) {

        if (messages == null || messages.isEmpty()) {
            ack.acknowledge();
            return;
        }

        Flux.fromIterable(messages)

                .filter(msg -> msg != null && !msg.isBlank())

                .flatMap(msg ->
                        Mono.fromCallable(() -> objectMapper.readTree(msg))
                                .subscribeOn(Schedulers.boundedElastic())
                                .onErrorResume(ex -> {
                                    log.warn("⚠ Invalid JSON skipped : {}", ex.getMessage());
                                    return Mono.empty();
                                }),
                        WRITE_CONCURRENCY
                )

                .flatMap(root -> {

                    final String meterType =
                            safe(root.path("meterType").asText(""));

                    final String meterNoTop =
                            safe(root.path("meterNo").asText(""));

                    final String trackingId =
                            safe(root.path("trackingId").asText(""));

                    final String systemTime =
                            safe(root.path("systemTime").asText(""));

                    Date mdasDatetime =
                            parseDateStrict(systemTime);

                    if (mdasDatetime == null) {

                        log.warn(
                                "⚠ Invalid systemTime meter={} value={}",
                                meterNoTop,
                                systemTime);

                        return Mono.empty();
                    }

                    JsonNode data = root.path("data");

                    JsonNode headersNode =
                            data.path("1");

                    JsonNode rowNode =
                            data.path("2");

                    if (!headersNode.isArray()
                            || !rowNode.isArray()
                            || headersNode.size() < 2
                            || rowNode.size() < 2) {

                        log.warn(
                                "⚠ Invalid payload meter={}",
                                meterNoTop);

                        return Mono.empty();
                    }

                    String meterFromRow =
                            safe(rowNode.get(0).asText(""));

                    String meterNoFinal =
                            meterFromRow.isEmpty()
                                    ? meterNoTop
                                    : meterFromRow;

                    String meterDatetimeStr =
                            safe(rowNode.get(1).asText(""));

                    Date meterDatetime =
                            parseDateStrict(meterDatetimeStr);

                    if (meterDatetime == null) {

                        log.warn(
                                "⚠ Invalid meter datetime meter={} value={}",
                                meterNoFinal,
                                meterDatetimeStr);

                        return Mono.empty();
                    }

                    if (isSinglePhase(meterType)) {

                        commandMetrics.incrementConsumed(
                                "InstantPushData-1P",
                                1);

                        InstantaneousPushSinglePhase entity =
                                buildSinglePhase(
                                        headersNode,
                                        rowNode,
                                        meterNoFinal,
                                        meterDatetime,
                                        mdasDatetime,
                                        trackingId);

                        return Mono.fromRunnable(() -> {

                                    try {

                                        singlePhaseRepo.save(entity);

                                        commandMetrics.incrementSaved(
                                                "InstantPushData-1P",
                                                1);

//                                        log.info(
//                                                "✅ PG 1P Saved meter={} meterDt={}",
//                                                meterNoFinal,
//                                                entity.getMeterDatetime());

                                    } catch (Exception ex) {

                                        log.error(
                                                "❌ PG 1P Save Failed meter={} err={}",
                                                meterNoFinal,
                                                ex.getMessage(),
                                                ex);
                                    }

                                })
                                .subscribeOn(
                                        Schedulers.boundedElastic());
                    }

                    if (isThreePhase(meterType)) {

                        commandMetrics.incrementConsumed(
                                "InstantPushData-3P",
                                1);

                        InstantaneousPushThreePhase entity =
                                buildThreePhase(
                                        headersNode,
                                        rowNode,
                                        meterNoFinal,
                                        meterDatetime,
                                        mdasDatetime,
                                        trackingId);

                        return Mono.fromRunnable(() -> {

                                    try {

                                        threePhaseRepo.save(entity);

                                        commandMetrics.incrementSaved(
                                                "InstantPushData-3P",
                                                1);

//                                        log.info(
//                                                "✅ PG 3P Saved meter={} meterDt={}",
//                                                meterNoFinal,
//                                                entity.getMeterDatetime());

                                    } catch (Exception ex) {

                                        log.error(
                                                "❌ PG 3P Save Failed meter={} err={}",
                                                meterNoFinal,
                                                ex.getMessage(),
                                                ex);
                                    }

                                })
                                .subscribeOn(
                                        Schedulers.boundedElastic());
                    }

                    log.warn(
                            "⚠ Unknown meter type={} meter={}",
                            meterType,
                            meterNoFinal);

                    return Mono.empty();

                }, WRITE_CONCURRENCY)

                .onBackpressureBuffer(
                        BUFFER_SIZE,
                        dropped -> log.warn(
                                "⚠ Buffer Full - Record Dropped"),
                        BufferOverflowStrategy.DROP_OLDEST)

                .retryWhen(
                        Retry.fixedDelay(
                                        MAX_RETRY,
                                        Duration.ofMillis(RETRY_DELAY_MS))
                                .doBeforeRetry(signal ->
                                        log.warn(
                                                "🔁 Retry due to {}",
                                                signal.failure().getMessage()))
                )

                .then()

                .doOnSuccess(v -> {

                    ack.acknowledge();

                    log.info(
                            "✅ Kafka Batch Acknowledged. records={}",
                            messages.size());
                })

                .doOnError(ex ->
                        log.error(
                                "❌ Pipeline Failed",
                                ex))

                .subscribe();
    }

    // ========================= Builders =========================

    private InstantaneousPushSinglePhase buildSinglePhase(
            JsonNode headers, JsonNode row,
            String meterNo,
            Date meterDatetime,
            Date mdasDatetime,
            String trackingId) {

    	InstantaneousPushSinglePhase e = new InstantaneousPushSinglePhase();

    	e.setDeviceSerialNumber(safe(meterNo));

    	e.setMeterDatetime(toLocalDateTime(meterDatetime));

    	e.setDatetime(toLocalDateTime(meterDatetime));   // <-- Missing

    	e.setMdasDatetime(toLocalDateTime(mdasDatetime));

    	e.setTrackingId(safe(trackingId));
        e.setReadingType(1);


    	e.setOwnerName(ObisFieldMapping.resolveOwner(safe(meterNo)));

    	e.setCreatedAt(LocalDateTime.now());             // <-- Missing

        for (int i = 0; i < headers.size() && i < row.size(); i++) {
            String key = headerKey(headers.get(i).asText(""));
            JsonNode valNode = row.get(i);

            Double d = asDouble(valNode);
            Integer n = asInt(valNode);

            // ---- Single Phase full mapping as per your header list ----
            if (key.equals("voltage")) e.setInstantVoltage(d);
            else if (key.equals("phase_current")) e.setPhaseCurrent(d);
            else if (key.equals("neutral_current")) e.setNeutralCurrent(d);
            else if (key.equals("pf")) e.setPowerFactor(d);
            else if (key.equals("active_power_kw")) e.setActivePowerKw(d);
            else if (key.equals("apparent_power_kva")) e.setApparentPowerKva(d);

            else if (key.equals("billing_count")) e.setCumulativeBillCount(n);

            else if (key.equals("energy_import_kvah")) e.setCumulativeEnergyKvahImport(d);
            else if (key.equals("energy_import_kwh")) e.setCumulativeEnergyKwhImport(d);

            // (If in future export fields come in 1P payload)
            else if (key.equals("energy_export_kvah")) e.setCumulativeEnergyKvahExport(d);
            else if (key.equals("energy_export_kwh")) e.setCumulativeEnergyKwhExport(d);

            else if (key.equals("tamper_count")) e.setTamperCount(n);
            else if (key.equals("frequency")) e.setFrequency(d);

            else if (key.equals("load_limit")) e.setLoadLimit(n);
            else if (key.equals("load_status")) e.setLoadLimitStatus(n);

            else if (key.equals("md_kva")) e.setMaximumDemandKva(d);
            else if (key.equals("md_kw")) e.setMaximumDemandKw(d);
        }

        return e;
    }

    private InstantaneousPushThreePhase buildThreePhase(
            JsonNode headers, JsonNode row,
            String meterNo,
            Date meterDatetime,
            Date mdasDatetime,
            String trackingId) {

    	InstantaneousPushThreePhase e = new InstantaneousPushThreePhase();

    	e.setDeviceSerialNumber(safe(meterNo));

    	e.setMeterDatetime(toLocalDateTime(meterDatetime));

    	e.setDatetime(toLocalDateTime(meterDatetime));

    	e.setMdasDatetime(toLocalDateTime(mdasDatetime));
        e.setReadingType(1);


    	e.setTrackingId(safe(trackingId));

    	e.setOwnerName(
    	        ObisFieldMapping.resolveOwner(
    	                safe(meterNo)));

    	e.setCreatedAt(LocalDateTime.now());

        for (int i = 0; i < headers.size() && i < row.size(); i++) {
            String key = headerKey(headers.get(i).asText(""));
            Double d = asDouble(row.get(i));

            // Voltages
            if (key.equals("r_ph_voltage")) e.setVoltageVrn(d);
            else if (key.equals("y_ph_voltage")) e.setVoltageVyn(d);
            else if (key.equals("b_ph_voltage")) e.setVoltageVbn(d);

            // Currents
            else if (key.equals("r_ph_current")) e.setCurrentIr(d);
            else if (key.equals("y_ph_current")) e.setCurrentIy(d);
            else if (key.equals("b_ph_current")) e.setCurrentIb(d);

            // Energies
            else if (key.equals("energy_import_kwh")) e.setActiveEnergyKwhImport(d);
            else if (key.equals("energy_export_kwh")) e.setActiveEnergyKwhExport(d);
            else if (key.equals("energy_import_kvah")) e.setApparentEnergyKvahImport(d);
            else if (key.equals("energy_export_kvah")) e.setApparentEnergyKvahExport(d);
        }

        return e;
    }

    // ========================= Helpers =========================

    private boolean isSinglePhase(String meterType) {
        if (meterType == null) return false;
        String mt = meterType.trim().toLowerCase();
        return mt.equals("single phase") || mt.equals("1p") || mt.contains("single");
    }

    private boolean isThreePhase(String meterType) {
        if (meterType == null) return false;
        String mt = meterType.trim().toLowerCase();
        return mt.equals("three phase") || mt.equals("3p") || mt.contains("three")
            || mt.equals("ct meter") || mt.equals("ht meter");
    }

    // ✅ STRICT: no fallback to new Date()
    private Date parseDateStrict(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            String cleaned = s.replace('T', ' ').trim();
            return DF.get().parse(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private Double asDouble(JsonNode n) {
        if (n == null || n.isNull()) return null;
        try {
            if (n.isNumber()) return n.doubleValue();
            String s = n.asText("").trim();
            if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
            s = s.replace(",", "");
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer asInt(JsonNode n) {
        if (n == null || n.isNull()) return null;
        try {
            if (n.isInt() || n.isLong()) return n.intValue();
            String s = n.asText("").trim();
            if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
            s = s.replace(",", "");
            Double d = Double.parseDouble(s); // handles "0.0"
            return d.intValue();
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
     * Normalize header to stable keys.
     */
    private String headerKey(String header) {
        if (header == null) return "";
        String h = header.trim().toLowerCase();
        h = h.replaceAll("\\s+", " ").trim();

        // Meter fields
        if (h.equals("meter s.no.") || h.equals("meter s.no") || h.equals("meter s.no.")) return "meter_no";
        if (h.equals("meter date time") || h.equals("meter datetime")) return "meter_datetime";

        // Single Phase basic
        if (h.equals("voltage")) return "voltage";
        if (h.equals("phase current")) return "phase_current";
        if (h.equals("neutral current")) return "neutral_current";
        if (h.equals("pf")) return "pf";
        if (h.equals("active power(kw)") || h.equals("active power (kw)")) return "active_power_kw";
        if (h.equals("apparent power(kva)") || h.equals("apparent power (kva)")) return "apparent_power_kva";
        if (h.equals("billing count")) return "billing_count";
        if (h.equals("tamper count")) return "tamper_count";
        if (h.equals("frequency")) return "frequency";
        if (h.equals("load limit")) return "load_limit";
        if (h.equals("load status")) return "load_status";
        if (h.equals("md(kva)") || h.equals("md (kva)")) return "md_kva";
        if (h.equals("md(kw)") || h.equals("md (kw)")) return "md_kw";

        // Three Phase volt/current
        if (h.equals("r ph voltage")) return "r_ph_voltage";
        if (h.equals("y ph voltage")) return "y_ph_voltage";
        if (h.equals("b ph voltage")) return "b_ph_voltage";
        if (h.equals("r ph current")) return "r_ph_current";
        if (h.equals("y ph current")) return "y_ph_current";
        if (h.equals("b ph current")) return "b_ph_current";

        // Energies
        if (h.equals("energy import(kwh)") || h.equals("energy import (kwh)")) return "energy_import_kwh";
        if (h.equals("energy export(kwh)") || h.equals("energy export (kwh)")) return "energy_export_kwh";
        if (h.equals("energy import(kvah)") || h.equals("energy import (kvah)")) return "energy_import_kvah";
        if (h.equals("energy export(kvah)") || h.equals("energy export (kvah)")) return "energy_export_kvah";

        // fallback normalize
        h = h.replace(".", "");
        h = h.replaceAll("[^a-z0-9_ ()]", "");
        h = h.replace("(", "_").replace(")", "");
        h = h.replaceAll("\\s+", "_");
        return h;
 
    }
    
    private LocalDateTime toLocalDateTime(Date date) {

        if (date == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault());
    }
}
