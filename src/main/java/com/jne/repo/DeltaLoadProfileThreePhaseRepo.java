


package com.jne.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jne.model.LoadProfileThreePhase;
import com.jne.model.LoadProfileThreePhaseId;


@Repository
public interface DeltaLoadProfileThreePhaseRepo
        extends JpaRepository<LoadProfileThreePhase, LoadProfileThreePhaseId> {

    @Transactional
    @Modifying
    @Query(value = """

        INSERT INTO public.load_profile_data_threephase (

            device_serial_number,
            interval_datetime,

            phase_current_l1,
            phase_current_l2,
            phase_current_l3,

            phase_voltage_l1,
            phase_voltage_l2,
            phase_voltage_l3,

            block_energy_kwh_import,
            block_energy_kwh_export,

            block_energy_kvah_import,
            block_energy_kvah_export,

            mdas_datetime

        )
        VALUES (

            :deviceSerialNumber,
            :intervalDatetime,

            :phaseCurrentL1,
            :phaseCurrentL2,
            :phaseCurrentL3,

            :phaseVoltageL1,
            :phaseVoltageL2,
            :phaseVoltageL3,

            :blockEnergyKwhImport,
            :blockEnergyKwhExport,

            :blockEnergyKvahImport,
            :blockEnergyKvahExport,

            :mdasDatetime

        )

        ON CONFLICT (
            interval_datetime,
            device_serial_number
        )

        DO NOTHING

    """, nativeQuery = true)

    int insertIgnore(

            @Param("deviceSerialNumber") String deviceSerialNumber,
            @Param("intervalDatetime") LocalDateTime intervalDatetime,

            @Param("phaseCurrentL1") Double phaseCurrentL1,
            @Param("phaseCurrentL2") Double phaseCurrentL2,
            @Param("phaseCurrentL3") Double phaseCurrentL3,

            @Param("phaseVoltageL1") Double phaseVoltageL1,
            @Param("phaseVoltageL2") Double phaseVoltageL2,
            @Param("phaseVoltageL3") Double phaseVoltageL3,

            @Param("blockEnergyKwhImport") Double blockEnergyKwhImport,
            @Param("blockEnergyKwhExport") Double blockEnergyKwhExport,

            @Param("blockEnergyKvahImport") Double blockEnergyKvahImport,
            @Param("blockEnergyKvahExport") Double blockEnergyKvahExport,

            @Param("mdasDatetime") LocalDateTime mdasDatetime
    );

    // get data
    
 // ✅ FAST (uses PRIMARY KEY index fully)
//    List<LoadProfileThreePhase>
//    findByIdDeviceSerialNumberAndIdIntervalDatetimeBetween(
//            String deviceSerialNumber,
//            LocalDateTime startDate,
//            LocalDateTime endDate
//    );
//
//    // ⚠️ Works ONLY if interval_datetime index exists
//    List<LoadProfileThreePhase>
//    findByIdIntervalDatetimeBetween(
//            LocalDateTime startDate,
//            LocalDateTime endDate
//    );
    
    
    
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
            FROM load_profile_data_threephase1
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
            FROM load_profile_data_threephase1
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