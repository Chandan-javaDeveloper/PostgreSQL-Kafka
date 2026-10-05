package com.jne.repo;



import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jne.model.Outage;
import com.jne.model_Id.OutageId;

@Repository
public interface OutageRepo
        extends JpaRepository<Outage, OutageId> {

    @Transactional
    @Modifying
    @Query(value = """
    INSERT INTO outage
    (
      device_serial_number,
      meter_datetime,
      command_type,
      data,
      event_status,
      groups,
      mdas_datetime,
      owner_name,
      tracking_id
    )
    VALUES
    (
      :deviceSerialNumber,
      :meterDatetime,
      :commandType,
      :data,
      :eventStatus,
      :groups,
      :mdasDatetime,
      :ownerName,
      :trackingId
    )
    ON CONFLICT
    (
      device_serial_number,
      meter_datetime
    )
    DO NOTHING
    """, nativeQuery = true)
    int insertIgnore(
            String deviceSerialNumber,
            LocalDateTime meterDatetime,
            String commandType,
            String data,
            String eventStatus,
            String groups,
            LocalDateTime mdasDatetime,
            String ownerName,
            String trackingId
    );
}