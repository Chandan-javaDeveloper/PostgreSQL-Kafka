package com.jne.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.DevicesIpPingLogs;
import com.jne.model_Id.DevicesIpPingLogsId;

@Repository
public interface DevicesIpPingLogsRepository
        extends JpaRepository<DevicesIpPingLogs, DevicesIpPingLogsId> {

    Optional<DevicesIpPingLogs> findByDeviceSerialNumberAndTrackingId(
            String deviceSerialNumber,
            String trackingId);
}