package com.jne.model;



import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "instantaneous_data_singlephase")
public class InstantaneousDataSinglePhase {

    @EmbeddedId
    private InstantaneousDataSinglePhaseId id;

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

   
    @Column(name = "enable_disable_load")
    private Integer enableDisableLoad;

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

    @Column(name = "phase_current")
    private Double phaseCurrent;

    @Column(name = "power_factor")
    private Double powerFactor;

    @Column(name = "tracking_id")
    private String trackingId;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

	public InstantaneousDataSinglePhase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InstantaneousDataSinglePhase(InstantaneousDataSinglePhaseId id, Double activePowerKw,
			Double apparentPowerKva, Integer cumulativeBillCount, Double cumulativeEnergyKvahExport,
			Double cumulativeEnergyKvahImport, Double cumulativeEnergyKwhExport, Double cumulativeEnergyKwhImport,
			Integer cumulativePowerOnDuration, Integer cumulativeProgramCount, Integer cumulativeTamperCount,
			Integer enableDisableLoad, Double frequency, Double instantVoltage,
			Integer loadLimit, Integer loadLimitStatus, Double maximumDemandKva, LocalDateTime maximumDemandKvaDatetime,
			Double maximumDemandKw, LocalDateTime maximumDemandKwDatetime, LocalDateTime mdasDatetime,
			Double neutralCurrent, Double phaseCurrent, Double powerFactor, String trackingId) {
		super();
		this.id = id;
		this.activePowerKw = activePowerKw;
		this.apparentPowerKva = apparentPowerKva;
		this.cumulativeBillCount = cumulativeBillCount;
		this.cumulativeEnergyKvahExport = cumulativeEnergyKvahExport;
		this.cumulativeEnergyKvahImport = cumulativeEnergyKvahImport;
		this.cumulativeEnergyKwhExport = cumulativeEnergyKwhExport;
		this.cumulativeEnergyKwhImport = cumulativeEnergyKwhImport;
		this.cumulativePowerOnDuration = cumulativePowerOnDuration;
		this.cumulativeProgramCount = cumulativeProgramCount;
		this.cumulativeTamperCount = cumulativeTamperCount;
		this.enableDisableLoad = enableDisableLoad;
		this.frequency = frequency;
		this.instantVoltage = instantVoltage;
		this.loadLimit = loadLimit;
		this.loadLimitStatus = loadLimitStatus;
		this.maximumDemandKva = maximumDemandKva;
		this.maximumDemandKvaDatetime = maximumDemandKvaDatetime;
		this.maximumDemandKw = maximumDemandKw;
		this.maximumDemandKwDatetime = maximumDemandKwDatetime;
		this.mdasDatetime = mdasDatetime;
		this.neutralCurrent = neutralCurrent;
		this.phaseCurrent = phaseCurrent;
		this.powerFactor = powerFactor;
		this.trackingId = trackingId;
	}

	public InstantaneousDataSinglePhaseId getId() {
		return id;
	}

	public void setId(InstantaneousDataSinglePhaseId id) {
		this.id = id;
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

	

	public Integer getEnableDisableLoad() {
		return enableDisableLoad;
	}

	public void setEnableDisableLoad(Integer enableDisableLoad) {
		this.enableDisableLoad = enableDisableLoad;
	}

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

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	
	

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}

	@Override
	public String toString() {
		return "InstantaneousDataSinglePhase [id=" + id + ", activePowerKw=" + activePowerKw + ", apparentPowerKva="
				+ apparentPowerKva + ", cumulativeBillCount=" + cumulativeBillCount + ", cumulativeEnergyKvahExport="
				+ cumulativeEnergyKvahExport + ", cumulativeEnergyKvahImport=" + cumulativeEnergyKvahImport
				+ ", cumulativeEnergyKwhExport=" + cumulativeEnergyKwhExport + ", cumulativeEnergyKwhImport="
				+ cumulativeEnergyKwhImport + ", cumulativePowerOnDuration=" + cumulativePowerOnDuration
				+ ", cumulativeProgramCount=" + cumulativeProgramCount + ", cumulativeTamperCount="
				+ cumulativeTamperCount + ", enableDisableLoad=" + enableDisableLoad + ", frequency=" + frequency
				+ ", instantVoltage=" + instantVoltage + ", loadLimit=" + loadLimit + ", loadLimitStatus="
				+ loadLimitStatus + ", maximumDemandKva=" + maximumDemandKva + ", maximumDemandKvaDatetime="
				+ maximumDemandKvaDatetime + ", maximumDemandKw=" + maximumDemandKw + ", maximumDemandKwDatetime="
				+ maximumDemandKwDatetime + ", mdasDatetime=" + mdasDatetime + ", neutralCurrent=" + neutralCurrent
				+ ", phaseCurrent=" + phaseCurrent + ", powerFactor=" + powerFactor + ", trackingId=" + trackingId
				+ "]";
	}

	
    
    
}
