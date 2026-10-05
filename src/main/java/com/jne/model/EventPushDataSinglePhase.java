package com.jne.model;



import java.time.LocalDateTime;

import com.jne.model_Id.EventPushDataSinglePhaseId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "event_push_data_singlephase",
    schema = "public"
)
@IdClass(EventPushDataSinglePhaseId.class)
public class EventPushDataSinglePhase {

    @Id
    @Column(name = "device_serial_number", length = 50)
    private String deviceSerialNumber;

    @Id
    @Column(name = "meter_datetime")
    private LocalDateTime meterDatetime;

    @Column(name = "command_type")
    private String commandType;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "dcu_serial_number")
    private String dcuSerialNumber;

    @Column(name = "dt_name")
    private String dtName;

    @Column(name = "event_status")
    private String eventStatus;

    @Column(name = "feeder_name")
    private String feederName;

    @Column(name = "groups")
    private String groups;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "subdevision_name")
    private String subdevisionName;

    @Column(name = "substation_name")
    private String substationName;

    @Column(name = "tracking_id")
    private String trackingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public LocalDateTime getMeterDatetime() {
		return meterDatetime;
	}

	public void setMeterDatetime(LocalDateTime meterDatetime) {
		this.meterDatetime = meterDatetime;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getDcuSerialNumber() {
		return dcuSerialNumber;
	}

	public void setDcuSerialNumber(String dcuSerialNumber) {
		this.dcuSerialNumber = dcuSerialNumber;
	}

	public String getDtName() {
		return dtName;
	}

	public void setDtName(String dtName) {
		this.dtName = dtName;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getFeederName() {
		return feederName;
	}

	public void setFeederName(String feederName) {
		this.feederName = feederName;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getSubdevisionName() {
		return subdevisionName;
	}

	public void setSubdevisionName(String subdevisionName) {
		this.subdevisionName = subdevisionName;
	}

	public String getSubstationName() {
		return substationName;
	}

	public void setSubstationName(String substationName) {
		this.substationName = substationName;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}
    
    

    // Getter Setter
}