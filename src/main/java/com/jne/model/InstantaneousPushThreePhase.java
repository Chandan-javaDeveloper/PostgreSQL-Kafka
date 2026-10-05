package com.jne.model;


import java.time.LocalDateTime;

import com.jne.model_Id.InstantaneousPushThreePhaseId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "instantaneous_push_threephase",
    schema = "public"
)
@IdClass(InstantaneousPushThreePhaseId.class)
public class InstantaneousPushThreePhase {

    @Id
    @Column(name = "device_serial_number", length = 50, nullable = false)
    private String deviceSerialNumber;

    @Id
    @Column(name = "meter_datetime", nullable = false)
    private LocalDateTime meterDatetime;

    @Column(name = "tracking_id", length = 100)
    private String trackingId;

    @Column(name = "active_energy_kwh_export")
    private Double activeEnergyKwhExport;

    @Column(name = "active_energy_kwh_import")
    private Double activeEnergyKwhImport;

    @Column(name = "active_power_kw")
    private Double activePowerKw;

    @Column(name = "apparent_energy_kvah_export")
    private Double apparentEnergyKvahExport;

    @Column(name = "apparent_energy_kvah_import")
    private Double apparentEnergyKvahImport;

    @Column(name = "apparent_power_kva")
    private Double apparentPowerKva;

    @Column(name = "billing_date")
    private LocalDateTime billingDate;

    @Column(name = "cumulative_billing_count")
    private Integer cumulativeBillingCount;

    @Column(name = "cumulative_energy_kvah_export")
    private Double cumulativeEnergyKvahExport;

    @Column(name = "cumulative_energy_kvah_import")
    private Double cumulativeEnergyKvahImport;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "cumulative_power_off_duration_in_minutes")
    private Integer cumulativePowerOffDurationInMinutes;

    @Column(name = "cumulative_programming_count")
    private Integer cumulativeProgrammingCount;

    @Column(name = "cumulative_tamper_count")
    private Integer cumulativeTamperCount;

    @Column(name = "current_ib")
    private Double currentIb;

    @Column(name = "current_ir")
    private Double currentIr;

    @Column(name = "current_iy")
    private Double currentIy;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    @Column(name = "enable_disable_load_limit_function")
    private Integer enableDisableLoadLimitFunction;

    @Column(name = "frequency")
    private Double frequency;

    @Column(name = "load_limit_kw")
    private Double loadLimitKw;

    @Column(name = "maximum_demand_kva")
    private Double maximumDemandKva;

    @Column(name = "maximum_demand_kva_date")
    private LocalDateTime maximumDemandKvaDate;

    @Column(name = "maximum_demand_kw")
    private Double maximumDemandKw;

    @Column(name = "maximum_demand_kw_date")
    private LocalDateTime maximumDemandKwDate;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "number_of_power_failures")
    private Integer numberOfPowerFailures;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "power_factor")
    private Double powerFactor;

    @Column(name = "power_factor_b_phase")
    private Double powerFactorBPhase;

    @Column(name = "power_factor_r_phase")
    private Double powerFactorRPhase;

    @Column(name = "power_factor_y_phase")
    private Double powerFactorYPhase;

    @Column(name = "reactive_energy_q1")
    private Double reactiveEnergyQ1;

    @Column(name = "reactive_energy_q2")
    private Double reactiveEnergyQ2;

    @Column(name = "reactive_energy_q3")
    private Double reactiveEnergyQ3;

    @Column(name = "reactive_energy_q4")
    private Double reactiveEnergyQ4;

    @Column(name = "reactive_power_kvar")
    private Double reactivePowerKvar;

    @Column(name = "rtc_datetime")
    private LocalDateTime rtcDatetime;

    @Column(name = "signed_reactive_power_kvar_lag_lead")
    private Double signedReactivePowerKvarLagLead;

    @Column(name = "voltage_vbn")
    private Double voltageVbn;

    @Column(name = "voltage_vrn")
    private Double voltageVrn;

    @Column(name = "voltage_vyn")
    private Double voltageVyn;

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

	public Double getActiveEnergyKwhExport() {
		return activeEnergyKwhExport;
	}

	public void setActiveEnergyKwhExport(Double activeEnergyKwhExport) {
		this.activeEnergyKwhExport = activeEnergyKwhExport;
	}

	public Double getActiveEnergyKwhImport() {
		return activeEnergyKwhImport;
	}

	public void setActiveEnergyKwhImport(Double activeEnergyKwhImport) {
		this.activeEnergyKwhImport = activeEnergyKwhImport;
	}

	public Double getActivePowerKw() {
		return activePowerKw;
	}

	public void setActivePowerKw(Double activePowerKw) {
		this.activePowerKw = activePowerKw;
	}

	public Double getApparentEnergyKvahExport() {
		return apparentEnergyKvahExport;
	}

	public void setApparentEnergyKvahExport(Double apparentEnergyKvahExport) {
		this.apparentEnergyKvahExport = apparentEnergyKvahExport;
	}

	public Double getApparentEnergyKvahImport() {
		return apparentEnergyKvahImport;
	}

	public void setApparentEnergyKvahImport(Double apparentEnergyKvahImport) {
		this.apparentEnergyKvahImport = apparentEnergyKvahImport;
	}

	public Double getApparentPowerKva() {
		return apparentPowerKva;
	}

	public void setApparentPowerKva(Double apparentPowerKva) {
		this.apparentPowerKva = apparentPowerKva;
	}

	public LocalDateTime getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(LocalDateTime billingDate) {
		this.billingDate = billingDate;
	}

	public Integer getCumulativeBillingCount() {
		return cumulativeBillingCount;
	}

	public void setCumulativeBillingCount(Integer cumulativeBillingCount) {
		this.cumulativeBillingCount = cumulativeBillingCount;
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

	public Integer getCumulativePowerOffDurationInMinutes() {
		return cumulativePowerOffDurationInMinutes;
	}

	public void setCumulativePowerOffDurationInMinutes(Integer cumulativePowerOffDurationInMinutes) {
		this.cumulativePowerOffDurationInMinutes = cumulativePowerOffDurationInMinutes;
	}

	public Integer getCumulativeProgrammingCount() {
		return cumulativeProgrammingCount;
	}

	public void setCumulativeProgrammingCount(Integer cumulativeProgrammingCount) {
		this.cumulativeProgrammingCount = cumulativeProgrammingCount;
	}

	public Integer getCumulativeTamperCount() {
		return cumulativeTamperCount;
	}

	public void setCumulativeTamperCount(Integer cumulativeTamperCount) {
		this.cumulativeTamperCount = cumulativeTamperCount;
	}

	public Double getCurrentIb() {
		return currentIb;
	}

	public void setCurrentIb(Double currentIb) {
		this.currentIb = currentIb;
	}

	public Double getCurrentIr() {
		return currentIr;
	}

	public void setCurrentIr(Double currentIr) {
		this.currentIr = currentIr;
	}

	public Double getCurrentIy() {
		return currentIy;
	}

	public void setCurrentIy(Double currentIy) {
		this.currentIy = currentIy;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public Integer getEnableDisableLoadLimitFunction() {
		return enableDisableLoadLimitFunction;
	}

	public void setEnableDisableLoadLimitFunction(Integer enableDisableLoadLimitFunction) {
		this.enableDisableLoadLimitFunction = enableDisableLoadLimitFunction;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Double getLoadLimitKw() {
		return loadLimitKw;
	}

	public void setLoadLimitKw(Double loadLimitKw) {
		this.loadLimitKw = loadLimitKw;
	}

	public Double getMaximumDemandKva() {
		return maximumDemandKva;
	}

	public void setMaximumDemandKva(Double maximumDemandKva) {
		this.maximumDemandKva = maximumDemandKva;
	}

	public LocalDateTime getMaximumDemandKvaDate() {
		return maximumDemandKvaDate;
	}

	public void setMaximumDemandKvaDate(LocalDateTime maximumDemandKvaDate) {
		this.maximumDemandKvaDate = maximumDemandKvaDate;
	}

	public Double getMaximumDemandKw() {
		return maximumDemandKw;
	}

	public void setMaximumDemandKw(Double maximumDemandKw) {
		this.maximumDemandKw = maximumDemandKw;
	}

	public LocalDateTime getMaximumDemandKwDate() {
		return maximumDemandKwDate;
	}

	public void setMaximumDemandKwDate(LocalDateTime maximumDemandKwDate) {
		this.maximumDemandKwDate = maximumDemandKwDate;
	}

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public Integer getNumberOfPowerFailures() {
		return numberOfPowerFailures;
	}

	public void setNumberOfPowerFailures(Integer numberOfPowerFailures) {
		this.numberOfPowerFailures = numberOfPowerFailures;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Double getPowerFactor() {
		return powerFactor;
	}

	public void setPowerFactor(Double powerFactor) {
		this.powerFactor = powerFactor;
	}

	public Double getPowerFactorBPhase() {
		return powerFactorBPhase;
	}

	public void setPowerFactorBPhase(Double powerFactorBPhase) {
		this.powerFactorBPhase = powerFactorBPhase;
	}

	public Double getPowerFactorRPhase() {
		return powerFactorRPhase;
	}

	public void setPowerFactorRPhase(Double powerFactorRPhase) {
		this.powerFactorRPhase = powerFactorRPhase;
	}

	public Double getPowerFactorYPhase() {
		return powerFactorYPhase;
	}

	public void setPowerFactorYPhase(Double powerFactorYPhase) {
		this.powerFactorYPhase = powerFactorYPhase;
	}

	public Double getReactiveEnergyQ1() {
		return reactiveEnergyQ1;
	}

	public void setReactiveEnergyQ1(Double reactiveEnergyQ1) {
		this.reactiveEnergyQ1 = reactiveEnergyQ1;
	}

	public Double getReactiveEnergyQ2() {
		return reactiveEnergyQ2;
	}

	public void setReactiveEnergyQ2(Double reactiveEnergyQ2) {
		this.reactiveEnergyQ2 = reactiveEnergyQ2;
	}

	public Double getReactiveEnergyQ3() {
		return reactiveEnergyQ3;
	}

	public void setReactiveEnergyQ3(Double reactiveEnergyQ3) {
		this.reactiveEnergyQ3 = reactiveEnergyQ3;
	}

	public Double getReactiveEnergyQ4() {
		return reactiveEnergyQ4;
	}

	public void setReactiveEnergyQ4(Double reactiveEnergyQ4) {
		this.reactiveEnergyQ4 = reactiveEnergyQ4;
	}

	public Double getReactivePowerKvar() {
		return reactivePowerKvar;
	}

	public void setReactivePowerKvar(Double reactivePowerKvar) {
		this.reactivePowerKvar = reactivePowerKvar;
	}

	public LocalDateTime getRtcDatetime() {
		return rtcDatetime;
	}

	public void setRtcDatetime(LocalDateTime rtcDatetime) {
		this.rtcDatetime = rtcDatetime;
	}

	public Double getSignedReactivePowerKvarLagLead() {
		return signedReactivePowerKvarLagLead;
	}

	public void setSignedReactivePowerKvarLagLead(Double signedReactivePowerKvarLagLead) {
		this.signedReactivePowerKvarLagLead = signedReactivePowerKvarLagLead;
	}

	public Double getVoltageVbn() {
		return voltageVbn;
	}

	public void setVoltageVbn(Double voltageVbn) {
		this.voltageVbn = voltageVbn;
	}

	public Double getVoltageVrn() {
		return voltageVrn;
	}

	public void setVoltageVrn(Double voltageVrn) {
		this.voltageVrn = voltageVrn;
	}

	public Double getVoltageVyn() {
		return voltageVyn;
	}

	public void setVoltageVyn(Double voltageVyn) {
		this.voltageVyn = voltageVyn;
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