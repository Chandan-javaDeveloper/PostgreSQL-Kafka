package com.jne.model;


import java.time.LocalDateTime;

import com.jne.model_Id.NamePlateId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "name_plate", schema = "public")
public class NamePlate {

    @EmbeddedId
    private NamePlateId id;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "current_ratings", length = 100)
    private String currentRatings;

    @Column(name = "dcu_serial_number", length = 100)
    private String dcuSerialNumber;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "dt_name")
    private String dtName;

    @Column(name = "feeder_name")
    private String feederName;

    @Column(name = "firmware_version", length = 100)
    private String firmwareVersion;

    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "manufacturer_year", length = 50)
    private String manufacturerYear;

    @Column(name = "meter_serial_number", length = 100)
    private String meterSerialNumber;

    @Column(name = "meter_type", length = 100)
    private String meterType;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "subdevision_name")
    private String subdevisionName;

    @Column(name = "substation_name")
    private String substationName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reading_type")
    private Integer ReadingType;
    
    public NamePlate() {}

    // getters and setters

    public NamePlateId getId() {
        return id;
    }

    public void setId(NamePlateId id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrentRatings() {
        return currentRatings;
    }

    public void setCurrentRatings(String currentRatings) {
        this.currentRatings = currentRatings;
    }

    public String getDcuSerialNumber() {
        return dcuSerialNumber;
    }

    public void setDcuSerialNumber(String dcuSerialNumber) {
        this.dcuSerialNumber = dcuSerialNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDtName() {
        return dtName;
    }

    public void setDtName(String dtName) {
        this.dtName = dtName;
    }

    public String getFeederName() {
        return feederName;
    }

    public void setFeederName(String feederName) {
        this.feederName = feederName;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getManufacturerYear() {
        return manufacturerYear;
    }

    public void setManufacturerYear(String manufacturerYear) {
        this.manufacturerYear = manufacturerYear;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    public void setMeterSerialNumber(String meterSerialNumber) {
        this.meterSerialNumber = meterSerialNumber;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    
    
}