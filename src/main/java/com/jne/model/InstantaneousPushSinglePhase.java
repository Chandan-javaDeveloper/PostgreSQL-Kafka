package com.jne.model;


import java.time.LocalDateTime;

import com.jne.model_Id.InstantaneousPushSinglePhaseId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "instantaneous_push_singlephase",
    schema = "public"
)
@IdClass(InstantaneousPushSinglePhaseId.class)
public class InstantaneousPushSinglePhase {

    @Id
    @Column(name = "device_serial_number", length = 50, nullable = false)
    private String deviceSerialNumber;

    @Id
    @Column(name = "meter_datetime", nullable = false)
    private LocalDateTime meterDatetime;

    @Column(name = "tracking_id", length = 100)
    private String trackingId;

    @Column(name = "active_power_kw")
    private Double activePowerKw;

    @Column(name = "apparent_power_kva")
    private Double apparentPowerKva;

    @Column(name = "cumulative_bill_count")
    private Integer cumulativeBillCount;

    @Column(name = "cumulative_energy_kvah_export")
    private Double cumulativeEnergyKvahExport;

    @Column(name = "cumulative_energy_kvah_import")
    private Double cumulativeEnergyKvahImport;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "cumulative_power_on_duration")
    private Integer cumulativePowerOnDuration;

    @Column(name = "cumulative_program_count")
    private Integer cumulativeProgramCount;

    @Column(name = "cumulative_tamper_count")
    private Integer cumulativeTamperCount;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    @Column(name = "dcu_serial_number", length = 100)
    private String dcuSerialNumber;

//    @Column(name = "dt_name")
//    private String dtName;

//    @Column(name = "feeder_name")
//    private String feederName;

    @Column(name = "frequency")
    private Double frequency;

    @Column(name = "instant_voltage")
    private Double instantVoltage;

    @Column(name = "load_limit")
    private Integer loadLimit;

    @Column(name = "load_limit_status")
    private Integer loadLimitStatus;

    @Column(name = "maximum_demand_kva")
    private Double maximumDemandKva;

    @Column(name = "maximum_demand_kva_datetime")
    private LocalDateTime maximumDemandKvaDatetime;

    @Column(name = "maximum_demand_kw")
    private Double maximumDemandKw;

    @Column(name = "maximum_demand_kw_datetime")
    private LocalDateTime maximumDemandKwDatetime;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "neutral_current")
    private Double neutralCurrent;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "phase_current")
    private Double phaseCurrent;

    @Column(name = "power_factor")
    private Double powerFactor;

//    @Column(name = "subdevision_name")
//    private String subdevisionName;
//
//    @Column(name = "substation_name")
//    private String substationName;

    @Column(name = "tamper_count")
    private Integer tamperCount;

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

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public Double getActivePowerKw() {
		return activePowerKw;
	}

	public void setActivePowerKw(Double activePowerKw) {
		this.activePowerKw = activePowerKw;
	}

	public Double getApparentPowerKva() {
		return apparentPowerKva;
	}

	public void setApparentPowerKva(Double apparentPowerKva) {
		this.apparentPowerKva = apparentPowerKva;
	}

	public Integer getCumulativeBillCount() {
		return cumulativeBillCount;
	}

	public void setCumulativeBillCount(Integer cumulativeBillCount) {
		this.cumulativeBillCount = cumulativeBillCount;
	}

	public Double getCumulativeEnergyKvahExport() {
		return cumulativeEnergyKvahExport;
	}

	public void setCumulativeEnergyKvahExport(Double cumulativeEnergyKvahExport) {
		this.cumulativeEnergyKvahExport = cumulativeEnergyKvahExport;
	}

	public Double getCumulativeEnergyKvahImport() {
		return cumulativeEnergyKvahImport;
	}

	public void setCumulativeEnergyKvahImport(Double cumulativeEnergyKvahImport) {
		this.cumulativeEnergyKvahImport = cumulativeEnergyKvahImport;
	}

	public Double getCumulativeEnergyKwhExport() {
		return cumulativeEnergyKwhExport;
	}

	public void setCumulativeEnergyKwhExport(Double cumulativeEnergyKwhExport) {
		this.cumulativeEnergyKwhExport = cumulativeEnergyKwhExport;
	}

	public Double getCumulativeEnergyKwhImport() {
		return cumulativeEnergyKwhImport;
	}

	public void setCumulativeEnergyKwhImport(Double cumulativeEnergyKwhImport) {
		this.cumulativeEnergyKwhImport = cumulativeEnergyKwhImport;
	}

	public Integer getCumulativePowerOnDuration() {
		return cumulativePowerOnDuration;
	}

	public void setCumulativePowerOnDuration(Integer cumulativePowerOnDuration) {
		this.cumulativePowerOnDuration = cumulativePowerOnDuration;
	}

	public Integer getCumulativeProgramCount() {
		return cumulativeProgramCount;
	}

	public void setCumulativeProgramCount(Integer cumulativeProgramCount) {
		this.cumulativeProgramCount = cumulativeProgramCount;
	}

	public Integer getCumulativeTamperCount() {
		return cumulativeTamperCount;
	}

	public void setCumulativeTamperCount(Integer cumulativeTamperCount) {
		this.cumulativeTamperCount = cumulativeTamperCount;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public String getDcuSerialNumber() {
		return dcuSerialNumber;
	}

	public void setDcuSerialNumber(String dcuSerialNumber) {
		this.dcuSerialNumber = dcuSerialNumber;
	}

//	public String getDtName() {
//		return dtName;
//	}
//
//	public void setDtName(String dtName) {
//		this.dtName = dtName;
//	}

//	public String getFeederName() {
//		return feederName;
//	}
//
//	public void setFeederName(String feederName) {
//		this.feederName = feederName;
//	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Double getInstantVoltage() {
		return instantVoltage;
	}

	public void setInstantVoltage(Double instantVoltage) {
		this.instantVoltage = instantVoltage;
	}

	public Integer getLoadLimit() {
		return loadLimit;
	}

	public void setLoadLimit(Integer loadLimit) {
		this.loadLimit = loadLimit;
	}

	public Integer getLoadLimitStatus() {
		return loadLimitStatus;
	}

	public void setLoadLimitStatus(Integer loadLimitStatus) {
		this.loadLimitStatus = loadLimitStatus;
	}

	public Double getMaximumDemandKva() {
		return maximumDemandKva;
	}

	public void setMaximumDemandKva(Double maximumDemandKva) {
		this.maximumDemandKva = maximumDemandKva;
	}

	public LocalDateTime getMaximumDemandKvaDatetime() {
		return maximumDemandKvaDatetime;
	}

	public void setMaximumDemandKvaDatetime(LocalDateTime maximumDemandKvaDatetime) {
		this.maximumDemandKvaDatetime = maximumDemandKvaDatetime;
	}

	public Double getMaximumDemandKw() {
		return maximumDemandKw;
	}

	public void setMaximumDemandKw(Double maximumDemandKw) {
		this.maximumDemandKw = maximumDemandKw;
	}

	public LocalDateTime getMaximumDemandKwDatetime() {
		return maximumDemandKwDatetime;
	}

	public void setMaximumDemandKwDatetime(LocalDateTime maximumDemandKwDatetime) {
		this.maximumDemandKwDatetime = maximumDemandKwDatetime;
	}

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public Double getNeutralCurrent() {
		return neutralCurrent;
	}

	public void setNeutralCurrent(Double neutralCurrent) {
		this.neutralCurrent = neutralCurrent;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Double getPhaseCurrent() {
		return phaseCurrent;
	}

	public void setPhaseCurrent(Double phaseCurrent) {
		this.phaseCurrent = phaseCurrent;
	}

	public Double getPowerFactor() {
		return powerFactor;
	}

	public void setPowerFactor(Double powerFactor) {
		this.powerFactor = powerFactor;
	}

//	public String getSubdevisionName() {
//		return subdevisionName;
//	}
//
//	public void setSubdevisionName(String subdevisionName) {
//		this.subdevisionName = subdevisionName;
//	}
//
//	public String getSubstationName() {
//		return substationName;
//	}
//
//	public void setSubstationName(String substationName) {
//		this.substationName = substationName;
//	}

	public Integer getTamperCount() {
		return tamperCount;
	}

	public void setTamperCount(Integer tamperCount) {
		this.tamperCount = tamperCount;
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