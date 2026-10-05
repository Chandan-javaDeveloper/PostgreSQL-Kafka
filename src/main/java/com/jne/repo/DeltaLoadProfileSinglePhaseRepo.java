package com.jne.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jne.model.LoadProfileSinglePhase;
import com.jne.model.LoadProfileSinglePhaseId;

@Repository
public interface DeltaLoadProfileSinglePhaseRepo
        extends JpaRepository<LoadProfileSinglePhase, LoadProfileSinglePhaseId> {

    // =========================================================
    // INSERT IGNORE
    // =========================================================

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = """

        INSERT INTO load_profile_data_singlephase (

            device_serial_number,
            interval_datetime,

            average_voltage,
            average_current,

            block_energy_kwh_import,
            block_energy_kwh_export,

            block_energy_kvah_import,
            block_energy_kvah_export,

            mdas_datetime,
            meter_datetime

        )
        VALUES (

            :deviceSerialNumber,
            :intervalDatetime,

            :averageVoltage,
            :averageCurrent,

            :blockEnergyKwhImport,
            :blockEnergyKwhExport,

            :blockEnergyKvahImport,
            :blockEnergyKvahExport,

            :mdasDatetime,
            :meterDatetime

        )

        ON CONFLICT (device_serial_number, interval_datetime)

        DO NOTHING

    """, nativeQuery = true)
    int insertIgnore(

            @Param("deviceSerialNumber")
            String deviceSerialNumber,

            @Param("intervalDatetime")
            LocalDateTime intervalDatetime,

            @Param("averageVoltage")
            Double averageVoltage,

            @Param("averageCurrent")
            Double averageCurrent,

            @Param("blockEnergyKwhImport")
            Double blockEnergyKwhImport,

            @Param("blockEnergyKwhExport")
            Double blockEnergyKwhExport,

            @Param("blockEnergyKvahImport")
            Double blockEnergyKvahImport,

            @Param("blockEnergyKvahExport")
            Double blockEnergyKvahExport,

            @Param("mdasDatetime")
            LocalDateTime mdasDatetime,

            @Param("meterDatetime")
            LocalDateTime meterDatetime
    );

    // =========================================================
    // GET ALL DATA
    // =========================================================

    @Query(value = """
            SELECT
                device_serial_number,
                interval_datetime,
                mdas_datetime,
                block_energy_kwh_import,
                block_energy_kvah_import
            FROM load_profile_data_singlephase1
            WHERE interval_datetime >= :fromDate
            AND interval_datetime < :toDate
            ORDER BY interval_datetime
            """,
            nativeQuery = true)
    List<Object[]> getLpDataBetweenDatesFast(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    // =========================================================
    // METER WISE
    // =========================================================

    @Query(value = """
            SELECT
                device_serial_number,
                interval_datetime,
                mdas_datetime,
                block_energy_kwh_import,
                block_energy_kvah_import
            FROM load_profile_data_singlephase1
            WHERE device_serial_number = :meterNo
            AND interval_datetime >= :fromDate
            AND interval_datetime < :toDate
            ORDER BY interval_datetime
            """,
            nativeQuery = true)
    List<Object[]> getLpDataByMeterAndDate(
            @Param("meterNo") String meterNo,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}