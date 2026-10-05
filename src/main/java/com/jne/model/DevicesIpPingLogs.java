package com.jne.model;



import java.time.LocalDateTime;

import com.jne.model_Id.DevicesIpPingLogsId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "devices_ip_ping_logs",
    schema = "public",
    indexes = {
        @Index(name = "idx_ping_mdas", columnList = "mdas_datetime"),
        @Index(name = "idx_ping_meter", columnList = "device_serial_number")
    }
)
@IdClass(DevicesIpPingLogsId.class)
public class DevicesIpPingLogs {

    @Id
    @Column(name = "tracking_id", nullable = false, length = 255)
    private String trackingId;

    @Id
    @Column(name = "device_serial_number", nullable = false, length = 100)
    private String deviceSerialNumber;

    @Id
    @Column(name = "mdas_datetime", nullable = false)
    private LocalDateTime mdasDatetime;

    @Column(name = "command_completion_datetime")
    private LocalDateTime commandCompletionDatetime;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "tot_attempts")
    private Integer totAttempts;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public LocalDateTime getCommandCompletionDatetime() {
		return commandCompletionDatetime;
	}

	public void setCommandCompletionDatetime(LocalDateTime commandCompletionDatetime) {
		this.commandCompletionDatetime = commandCompletionDatetime;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTotAttempts() {
		return totAttempts;
	}

	public void setTotAttempts(Integer totAttempts) {
		this.totAttempts = totAttempts;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}