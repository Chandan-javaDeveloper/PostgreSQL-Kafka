package com.jne.ui_service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jne.repo.DeltaLoadProfileSinglePhaseRepo;
import com.jne.repo.DeltaLoadProfileThreePhaseRepo;
import com.jne.request.MeterRequestTime;
import com.jne.response.DeltaLPRes;

@Service
public class DeltaUiService {

    @Autowired
    private DeltaLoadProfileThreePhaseRepo tpRepo;

    @Autowired
    private DeltaLoadProfileSinglePhaseRepo spRepo;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // =========================================================
    // MAIN
    // =========================================================

    public List<DeltaLPRes> getDeltaLoadProfile(MeterRequestTime req) {

        LocalDateTime start = req.getStartDate();
        LocalDateTime end = req.getEndDate();

        boolean isThree = "3P".equalsIgnoreCase(req.getDevType());

        boolean isAll =
                req.getLevelName() != null
                && req.getLevelName().equalsIgnoreCase("ALL");

        return isThree
                ? getThreePhaseData(req, start, end, isAll)
                : getSinglePhaseData(req, start, end, isAll);
    }

    // =========================================================
    // THREE PHASE
    // =========================================================

    private List<DeltaLPRes> getThreePhaseData(
            MeterRequestTime req,
            LocalDateTime start,
            LocalDateTime end,
            boolean isAll) {

        List<Object[]> rows = isAll
                ? tpRepo.getLpDataBetweenDatesFast(start, end)
                : tpRepo.getLpDataByMeterAndDate(
                        req.getLevelValue(),
                        start,
                        end);

        List<DeltaLPRes> out = new ArrayList<>(rows.size());

        for (Object[] r : rows) {

            DeltaLPRes res = new DeltaLPRes();

            res.setDeviceSno(str(r[0]));

            res.setIntervalDatetime(
                    formatTimestamp(r[1]));

            res.setMdastDatetime(
                    formatTimestamp(r[2]));

            res.setBlockEnergyImportKwh(dbl(r[3]));
            res.setBlockEnergyImportKvah(dbl(r[4]));

            out.add(res);
        }

        return out;
    }

    // =========================================================
    // SINGLE PHASE
    // =========================================================

    private List<DeltaLPRes> getSinglePhaseData(
            MeterRequestTime req,
            LocalDateTime start,
            LocalDateTime end,
            boolean isAll) {

        List<Object[]> rows = isAll
                ? spRepo.getLpDataBetweenDatesFast(start, end)
                : spRepo.getLpDataByMeterAndDate(
                        req.getLevelValue(),
                        start,
                        end);

        List<DeltaLPRes> out = new ArrayList<>(rows.size());

        for (Object[] r : rows) {

            DeltaLPRes res = new DeltaLPRes();

            res.setDeviceSno(str(r[0]));

            res.setIntervalDatetime(
                    formatTimestamp(r[1]));

            res.setMdastDatetime(
                    formatTimestamp(r[2]));

            res.setBlockEnergyImportKwh(dbl(r[3]));
            res.setBlockEnergyImportKvah(dbl(r[4]));

            out.add(res);
        }

        return out;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private double dbl(Object o) {
        return o == null ? 0.0 : ((Number) o).doubleValue();
    }

    private String formatTimestamp(Object o) {

        if (o == null) {
            return "";
        }

        if (o instanceof Timestamp ts) {
            return ts.toLocalDateTime().format(FORMATTER);
        }

        if (o instanceof LocalDateTime ldt) {
            return ldt.format(FORMATTER);
        }

        return o.toString();
    }
}