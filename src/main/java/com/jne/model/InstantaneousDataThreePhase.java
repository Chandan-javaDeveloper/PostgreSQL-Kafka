package com.jne.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "instantaneous_data_threephase")
public class InstantaneousDataThreePhase {

    @EmbeddedId
    private InstantaneousDataThreePhaseId id;

    @Column(name = "active_power_kw")
    private Double activePowerKw;

    @Column(name = "apparent_power_kva")
    private Double apparentPowerKva;

    @Column(name = "cumm_power_off_duration_in_mins")
    private Double cummPowerOffDurationInMins;

    @Column(name = "cumulative_bill_count")
    private Integer cumulativeBillCount;

    @Column(name = "cumulative_energy_kvah_export")
    private Double cumulativeEnergyKvahExport;

    @Column(name = "cumulative_energy_kvah_import")
    private Double cumulativeEnergyKvahImport;

    @Column(name = "cumulative_energy_kvarh_q1")
    private Double cumulativeEnergyKvarhQ1;

    @Column(name = "cumulative_energy_kvarh_q2")
    private Double cumulativeEnergyKvarhQ2;

    @Column(name = "cumulative_energy_kvarh_q3")
    private Double cumulativeEnergyKvarhQ3;

    @Column(name = "cumulative_energy_kvarh_q4")
    private Double cumulativeEnergyKvarhQ4;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "cumulative_program_count")
    private Integer cumulativeProgramCount;

    @Column(name = "cumulative_tamper_count")
    private Integer cumulativeTamperCount;

   
    @Column(name = "enable_disable_load")
    private Integer enableDisableLoad;

    @Column(name = "frequency")
    private Double frequency;

    @Column(name = "instant_voltage_l1")
    private Double instantVoltageL1;

    @Column(name = "instant_voltage_l2")
    private Double instantVoltageL2;

    @Column(name = "instant_voltage_l3")
    private Double instantVoltageL3;

    @Column(name = "last_billing_datetime")
    private LocalDateTime lastBillingDatetime;

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

    @Column(name = "no_of_power_failure")
    private Integer noOfPowerFailure;

    @Column(name = "phase_current_l1")
    private Double phaseCurrentL1;

    @Column(name = "phase_current_l2")
    private Double phaseCurrentL2;

    @Column(name = "phase_current_l3")
    private Double phaseCurrentL3;

    @Column(name = "power_factor_3ph")
    private Double powerFactor3ph;

    @Column(name = "power_factor_l1")
    private Double powerFactorL1;

    @Column(name = "power_factor_l2")
    private Double powerFactorL2;

    @Column(name = "power_factor_l3")
    private Double powerFactorL3;

    @Column(name = "reactive_power_kvar")
    private Double reactivePowerKvar;

//    @Column(name = "total_pf")
//    private Double totalPf;

    @Column(name = "tracking_id")
    private String trackingId;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

	public InstantaneousDataThreePhase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InstantaneousDataThreePhase(InstantaneousDataThreePhaseId id, Double activePowerKw, Double apparentPowerKva,
			Double cummPowerOffDurationInMins, Integer cumulativeBillCount, Double cumulativeEnergyKvahExport,
			Double cumulativeEnergyKvahImport, Double cumulativeEnergyKvarhQ1, Double cumulativeEnergyKvarhQ2,
			Double cumulativeEnergyKvarhQ3, Double cumulativeEnergyKvarhQ4, Double cumulativeEnergyKwhExport,
			Double cumulativeEnergyKwhImport, Integer cumulativeProgramCount, Integer cumulativeTamperCount,
			Integer enableDisableLoad, Double frequency, Double instantVoltageL1,
			Double instantVoltageL2, Double instantVoltageL3, LocalDateTime lastBillingDatetime, Integer loadLimit,
			Integer loadLimitStatus, Double maximumDemandKva, LocalDateTime maximumDemandKvaDatetime,
			Double maximumDemandKw, LocalDateTime maximumDemandKwDatetime, LocalDateTime mdasDatetime,
			Integer noOfPowerFailure, Double phaseCurrentL1, Double phaseCurrentL2, Double phaseCurrentL3,
			Double powerFactor3ph, Double powerFactorL1, Double powerFactorL2, Double powerFactorL3,
			Double reactivePowerKvar, Double totalPf, String trackingId) {
		super();
		this.id = id;
		this.activePowerKw = activePowerKw;
		this.apparentPowerKva = apparentPowerKva;
		this.cummPowerOffDurationInMins = cummPowerOffDurationInMins;
		this.cumulativeBillCount = cumulativeBillCount;
		this.cumulativeEnergyKvahExport = cumulativeEnergyKvahExport;
		this.cumulativeEnergyKvahImport = cumulativeEnergyKvahImport;
		this.cumulativeEnergyKvarhQ1 = cumulativeEnergyKvarhQ1;
		this.cumulativeEnergyKvarhQ2 = cumulativeEnergyKvarhQ2;
		this.cumulativeEnergyKvarhQ3 = cumulativeEnergyKvarhQ3;
		this.cumulativeEnergyKvarhQ4 = cumulativeEnergyKvarhQ4;
		this.cumulativeEnergyKwhExport = cumulativeEnergyKwhExport;
		this.cumulativeEnergyKwhImport = cumulativeEnergyKwhImport;
		this.cumulativeProgramCount = cumulativeProgramCount;
		this.cumulativeTamperCount = cumulativeTamperCount;
		this.enableDisableLoad = enableDisableLoad;
		this.frequency = frequency;
		this.instantVoltageL1 = instantVoltageL1;
		this.instantVoltageL2 = instantVoltageL2;
		this.instantVoltageL3 = instantVoltageL3;
		this.lastBillingDatetime = lastBillingDatetime;
		this.loadLimit = loadLimit;
		this.loadLimitStatus = loadLimitStatus;
		this.maximumDemandKva = maximumDemandKva;
		this.maximumDemandKvaDatetime = maximumDemandKvaDatetime;
		this.maximumDemandKw = maximumDemandKw;
		this.maximumDemandKwDatetime = maximumDemandKwDatetime;
		this.mdasDatetime = mdasDatetime;
		this.noOfPowerFailure = noOfPowerFailure;
		this.phaseCurrentL1 = phaseCurrentL1;
		this.phaseCurrentL2 = phaseCurrentL2;
		this.phaseCurrentL3 = phaseCurrentL3;
		this.powerFactor3ph = powerFactor3ph;
		this.powerFactorL1 = powerFactorL1;
		this.powerFactorL2 = powerFactorL2;
		this.powerFactorL3 = powerFactorL3;
		this.reactivePowerKvar = reactivePowerKvar;
		this.trackingId = trackingId;
	}

	public InstantaneousDataThreePhaseId getId() {
		return id;
	}

	public void setId(InstantaneousDataThreePhaseId id) {
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

	public Double getCummPowerOffDurationInMins() {
		return cummPowerOffDurationInMins;
	}

	public void setCummPowerOffDurationInMins(Double cummPowerOffDurationInMins) {
		this.cummPowerOffDurationInMins = cummPowerOffDurationInMins;
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

	public Double getCumulativeEnergyKvarhQ1() {
		return cumulativeEnergyKvarhQ1;
	}

	public void setCumulativeEnergyKvarhQ1(Double cumulativeEnergyKvarhQ1) {
		this.cumulativeEnergyKvarhQ1 = cumulativeEnergyKvarhQ1;
	}

	public Double getCumulativeEnergyKvarhQ2() {
		return cumulativeEnergyKvarhQ2;
	}

	public void setCumulativeEnergyKvarhQ2(Double cumulativeEnergyKvarhQ2) {
		this.cumulativeEnergyKvarhQ2 = cumulativeEnergyKvarhQ2;
	}

	public Double getCumulativeEnergyKvarhQ3() {
		return cumulativeEnergyKvarhQ3;
	}

	public void setCumulativeEnergyKvarhQ3(Double cumulativeEnergyKvarhQ3) {
		this.cumulativeEnergyKvarhQ3 = cumulativeEnergyKvarhQ3;
	}

	public Double getCumulativeEnergyKvarhQ4() {
		return cumulativeEnergyKvarhQ4;
	}

	public void setCumulativeEnergyKvarhQ4(Double cumulativeEnergyKvarhQ4) {
		this.cumulativeEnergyKvarhQ4 = cumulativeEnergyKvarhQ4;
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

	public Double getInstantVoltageL1() {
		return instantVoltageL1;
	}

	public void setInstantVoltageL1(Double instantVoltageL1) {
		this.instantVoltageL1 = instantVoltageL1;
	}

	public Double getInstantVoltageL2() {
		return instantVoltageL2;
	}

	public void setInstantVoltageL2(Double instantVoltageL2) {
		this.instantVoltageL2 = instantVoltageL2;
	}

	public Double getInstantVoltageL3() {
		return instantVoltageL3;
	}

	public void setInstantVoltageL3(Double instantVoltageL3) {
		this.instantVoltageL3 = instantVoltageL3;
	}

	public LocalDateTime getLastBillingDatetime() {
		return lastBillingDatetime;
	}

	public void setLastBillingDatetime(LocalDateTime lastBillingDatetime) {
		this.lastBillingDatetime = lastBillingDatetime;
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

	public Integer getNoOfPowerFailure() {
		return noOfPowerFailure;
	}

	public void setNoOfPowerFailure(Integer noOfPowerFailure) {
		this.noOfPowerFailure = noOfPowerFailure;
	}

	public Double getPhaseCurrentL1() {
		return phaseCurrentL1;
	}

	public void setPhaseCurrentL1(Double phaseCurrentL1) {
		this.phaseCurrentL1 = phaseCurrentL1;
	}

	public Double getPhaseCurrentL2() {
		return phaseCurrentL2;
	}

	public void setPhaseCurrentL2(Double phaseCurrentL2) {
		this.phaseCurrentL2 = phaseCurrentL2;
	}

	public Double getPhaseCurrentL3() {
		return phaseCurrentL3;
	}

	public void setPhaseCurrentL3(Double phaseCurrentL3) {
		this.phaseCurrentL3 = phaseCurrentL3;
	}

	public Double getPowerFactor3ph() {
		return powerFactor3ph;
	}

	public void setPowerFactor3ph(Double powerFactor3ph) {
		this.powerFactor3ph = powerFactor3ph;
	}

	public Double getPowerFactorL1() {
		return powerFactorL1;
	}

	public void setPowerFactorL1(Double powerFactorL1) {
		this.powerFactorL1 = powerFactorL1;
	}

	public Double getPowerFactorL2() {
		return powerFactorL2;
	}

	public void setPowerFactorL2(Double powerFactorL2) {
		this.powerFactorL2 = powerFactorL2;
	}

	public Double getPowerFactorL3() {
		return powerFactorL3;
	}

	public void setPowerFactorL3(Double powerFactorL3) {
		this.powerFactorL3 = powerFactorL3;
	}

	public Double getReactivePowerKvar() {
		return reactivePowerKvar;
	}

	public void setReactivePowerKvar(Double reactivePowerKvar) {
		this.reactivePowerKvar = reactivePowerKvar;
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
		return "InstantaneousDataThreePhase [id=" + id + ", activePowerKw=" + activePowerKw + ", apparentPowerKva="
				+ apparentPowerKva + ", cummPowerOffDurationInMins=" + cummPowerOffDurationInMins
				+ ", cumulativeBillCount=" + cumulativeBillCount + ", cumulativeEnergyKvahExport="
				+ cumulativeEnergyKvahExport + ", cumulativeEnergyKvahImport=" + cumulativeEnergyKvahImport
				+ ", cumulativeEnergyKvarhQ1=" + cumulativeEnergyKvarhQ1 + ", cumulativeEnergyKvarhQ2="
				+ cumulativeEnergyKvarhQ2 + ", cumulativeEnergyKvarhQ3=" + cumulativeEnergyKvarhQ3
				+ ", cumulativeEnergyKvarhQ4=" + cumulativeEnergyKvarhQ4 + ", cumulativeEnergyKwhExport="
				+ cumulativeEnergyKwhExport + ", cumulativeEnergyKwhImport=" + cumulativeEnergyKwhImport
				+ ", cumulativeProgramCount=" + cumulativeProgramCount + ", cumulativeTamperCount="
				+ cumulativeTamperCount + ", enableDisableLoad=" + enableDisableLoad + ", frequency=" + frequency
				+ ", instantVoltageL1=" + instantVoltageL1 + ", instantVoltageL2=" + instantVoltageL2
				+ ", instantVoltageL3=" + instantVoltageL3 + ", lastBillingDatetime=" + lastBillingDatetime
				+ ", loadLimit=" + loadLimit + ", loadLimitStatus=" + loadLimitStatus + ", maximumDemandKva="
				+ maximumDemandKva + ", maximumDemandKvaDatetime=" + maximumDemandKvaDatetime + ", maximumDemandKw="
				+ maximumDemandKw + ", maximumDemandKwDatetime=" + maximumDemandKwDatetime + ", mdasDatetime="
				+ mdasDatetime + ", noOfPowerFailure=" + noOfPowerFailure + ", phaseCurrentL1=" + phaseCurrentL1
				+ ", phaseCurrentL2=" + phaseCurrentL2 + ", phaseCurrentL3=" + phaseCurrentL3 + ", powerFactor3ph="
				+ powerFactor3ph + ", powerFactorL1=" + powerFactorL1 + ", powerFactorL2=" + powerFactorL2
				+ ", powerFactorL3=" + powerFactorL3 + ", reactivePowerKvar=" + reactivePowerKvar + ", totalPf="
				+ ", trackingId=" + trackingId + "]";
	}

	
    
}
