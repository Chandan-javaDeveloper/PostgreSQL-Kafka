package com.jne.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "billing_data_singlephase")
public class BillingDataSinglePhase {

    @EmbeddedId
    private BillingDataSinglePhaseId id;

    @Column(name = "average_power_factor_for_billing_period")
    private Double averagePowerFactorForBillingPeriod;

    @Column(name = "billing_power_off_duration_in_billing")
    private Double billingPowerOffDurationInBilling;

    @Column(name = "billing_power_on_duration_in_billing")
    private Double billingPowerOnDurationInBilling;

    @Column(name = "cumulative_energy_kvah_export")
    private Double cumulativeEnergyKvahExport;

    @Column(name = "cumulative_energy_kvah_import")
    private Double cumulativeEnergyKvahImport;

    @Column(name = "cumulative_energy_kvah_tier1")
    private Double cumulativeEnergyKvahTier1;

    @Column(name = "cumulative_energy_kvah_tier2")
    private Double cumulativeEnergyKvahTier2;

    @Column(name = "cumulative_energy_kvah_tier3")
    private Double cumulativeEnergyKvahTier3;

    @Column(name = "cumulative_energy_kvah_tier4")
    private Double cumulativeEnergyKvahTier4;

    @Column(name = "cumulative_energy_kvah_tier5")
    private Double cumulativeEnergyKvahTier5;

    @Column(name = "cumulative_energy_kvah_tier6")
    private Double cumulativeEnergyKvahTier6;

    @Column(name = "cumulative_energy_kvah_tier7")
    private Double cumulativeEnergyKvahTier7;

    @Column(name = "cumulative_energy_kvah_tier8")
    private Double cumulativeEnergyKvahTier8;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "cumulative_energy_kwh_tier1")
    private Double cumulativeEnergyKwhTier1;

    @Column(name = "cumulative_energy_kwh_tier2")
    private Double cumulativeEnergyKwhTier2;

    @Column(name = "cumulative_energy_kwh_tier3")
    private Double cumulativeEnergyKwhTier3;

    @Column(name = "cumulative_energy_kwh_tier4")
    private Double cumulativeEnergyKwhTier4;

    @Column(name = "cumulative_energy_kwh_tier5")
    private Double cumulativeEnergyKwhTier5;

    @Column(name = "cumulative_energy_kwh_tier6")
    private Double cumulativeEnergyKwhTier6;

    @Column(name = "cumulative_energy_kwh_tier7")
    private Double cumulativeEnergyKwhTier7;

    @Column(name = "cumulative_energy_kwh_tier8")
    private Double cumulativeEnergyKwhTier8;

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
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    // Getters and Setters

    public BillingDataSinglePhaseId getId() {
        return id;
    }

    public void setId(BillingDataSinglePhaseId id) {
        this.id = id;
    }

	public Double getAveragePowerFactorForBillingPeriod() {
		return averagePowerFactorForBillingPeriod;
	}

	public void setAveragePowerFactorForBillingPeriod(Double averagePowerFactorForBillingPeriod) {
		this.averagePowerFactorForBillingPeriod = averagePowerFactorForBillingPeriod;
	}

	public Double getBillingPowerOffDurationInBilling() {
		return billingPowerOffDurationInBilling;
	}

	public void setBillingPowerOffDurationInBilling(Double billingPowerOffDurationInBilling) {
		this.billingPowerOffDurationInBilling = billingPowerOffDurationInBilling;
	}

	public Double getBillingPowerOnDurationInBilling() {
		return billingPowerOnDurationInBilling;
	}

	public void setBillingPowerOnDurationInBilling(Double billingPowerOnDurationInBilling) {
		this.billingPowerOnDurationInBilling = billingPowerOnDurationInBilling;
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

	public Double getCumulativeEnergyKvahTier1() {
		return cumulativeEnergyKvahTier1;
	}

	public void setCumulativeEnergyKvahTier1(Double cumulativeEnergyKvahTier1) {
		this.cumulativeEnergyKvahTier1 = cumulativeEnergyKvahTier1;
	}

	public Double getCumulativeEnergyKvahTier2() {
		return cumulativeEnergyKvahTier2;
	}

	public void setCumulativeEnergyKvahTier2(Double cumulativeEnergyKvahTier2) {
		this.cumulativeEnergyKvahTier2 = cumulativeEnergyKvahTier2;
	}

	public Double getCumulativeEnergyKvahTier3() {
		return cumulativeEnergyKvahTier3;
	}

	public void setCumulativeEnergyKvahTier3(Double cumulativeEnergyKvahTier3) {
		this.cumulativeEnergyKvahTier3 = cumulativeEnergyKvahTier3;
	}

	public Double getCumulativeEnergyKvahTier4() {
		return cumulativeEnergyKvahTier4;
	}

	public void setCumulativeEnergyKvahTier4(Double cumulativeEnergyKvahTier4) {
		this.cumulativeEnergyKvahTier4 = cumulativeEnergyKvahTier4;
	}

	public Double getCumulativeEnergyKvahTier5() {
		return cumulativeEnergyKvahTier5;
	}

	public void setCumulativeEnergyKvahTier5(Double cumulativeEnergyKvahTier5) {
		this.cumulativeEnergyKvahTier5 = cumulativeEnergyKvahTier5;
	}

	public Double getCumulativeEnergyKvahTier6() {
		return cumulativeEnergyKvahTier6;
	}

	public void setCumulativeEnergyKvahTier6(Double cumulativeEnergyKvahTier6) {
		this.cumulativeEnergyKvahTier6 = cumulativeEnergyKvahTier6;
	}

	public Double getCumulativeEnergyKvahTier7() {
		return cumulativeEnergyKvahTier7;
	}

	public void setCumulativeEnergyKvahTier7(Double cumulativeEnergyKvahTier7) {
		this.cumulativeEnergyKvahTier7 = cumulativeEnergyKvahTier7;
	}

	public Double getCumulativeEnergyKvahTier8() {
		return cumulativeEnergyKvahTier8;
	}

	public void setCumulativeEnergyKvahTier8(Double cumulativeEnergyKvahTier8) {
		this.cumulativeEnergyKvahTier8 = cumulativeEnergyKvahTier8;
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

	public Double getCumulativeEnergyKwhTier1() {
		return cumulativeEnergyKwhTier1;
	}

	public void setCumulativeEnergyKwhTier1(Double cumulativeEnergyKwhTier1) {
		this.cumulativeEnergyKwhTier1 = cumulativeEnergyKwhTier1;
	}

	public Double getCumulativeEnergyKwhTier2() {
		return cumulativeEnergyKwhTier2;
	}

	public void setCumulativeEnergyKwhTier2(Double cumulativeEnergyKwhTier2) {
		this.cumulativeEnergyKwhTier2 = cumulativeEnergyKwhTier2;
	}

	public Double getCumulativeEnergyKwhTier3() {
		return cumulativeEnergyKwhTier3;
	}

	public void setCumulativeEnergyKwhTier3(Double cumulativeEnergyKwhTier3) {
		this.cumulativeEnergyKwhTier3 = cumulativeEnergyKwhTier3;
	}

	public Double getCumulativeEnergyKwhTier4() {
		return cumulativeEnergyKwhTier4;
	}

	public void setCumulativeEnergyKwhTier4(Double cumulativeEnergyKwhTier4) {
		this.cumulativeEnergyKwhTier4 = cumulativeEnergyKwhTier4;
	}

	public Double getCumulativeEnergyKwhTier5() {
		return cumulativeEnergyKwhTier5;
	}

	public void setCumulativeEnergyKwhTier5(Double cumulativeEnergyKwhTier5) {
		this.cumulativeEnergyKwhTier5 = cumulativeEnergyKwhTier5;
	}

	public Double getCumulativeEnergyKwhTier6() {
		return cumulativeEnergyKwhTier6;
	}

	public void setCumulativeEnergyKwhTier6(Double cumulativeEnergyKwhTier6) {
		this.cumulativeEnergyKwhTier6 = cumulativeEnergyKwhTier6;
	}

	public Double getCumulativeEnergyKwhTier7() {
		return cumulativeEnergyKwhTier7;
	}

	public void setCumulativeEnergyKwhTier7(Double cumulativeEnergyKwhTier7) {
		this.cumulativeEnergyKwhTier7 = cumulativeEnergyKwhTier7;
	}

	public Double getCumulativeEnergyKwhTier8() {
		return cumulativeEnergyKwhTier8;
	}

	public void setCumulativeEnergyKwhTier8(Double cumulativeEnergyKwhTier8) {
		this.cumulativeEnergyKwhTier8 = cumulativeEnergyKwhTier8;
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
	
	

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}

	@Override
	public String toString() {
		return "BillingDataSinglePhase [id=" + id + ", averagePowerFactorForBillingPeriod="
				+ averagePowerFactorForBillingPeriod + ", billingPowerOffDurationInBilling="
				+ billingPowerOffDurationInBilling + ", billingPowerOnDurationInBilling="
				+ billingPowerOnDurationInBilling + ", cumulativeEnergyKvahExport=" + cumulativeEnergyKvahExport
				+ ", cumulativeEnergyKvahImport=" + cumulativeEnergyKvahImport + ", cumulativeEnergyKvahTier1="
				+ cumulativeEnergyKvahTier1 + ", cumulativeEnergyKvahTier2=" + cumulativeEnergyKvahTier2
				+ ", cumulativeEnergyKvahTier3=" + cumulativeEnergyKvahTier3 + ", cumulativeEnergyKvahTier4="
				+ cumulativeEnergyKvahTier4 + ", cumulativeEnergyKvahTier5=" + cumulativeEnergyKvahTier5
				+ ", cumulativeEnergyKvahTier6=" + cumulativeEnergyKvahTier6 + ", cumulativeEnergyKvahTier7="
				+ cumulativeEnergyKvahTier7 + ", cumulativeEnergyKvahTier8=" + cumulativeEnergyKvahTier8
				+ ", cumulativeEnergyKwhExport=" + cumulativeEnergyKwhExport + ", cumulativeEnergyKwhImport="
				+ cumulativeEnergyKwhImport + ", cumulativeEnergyKwhTier1=" + cumulativeEnergyKwhTier1
				+ ", cumulativeEnergyKwhTier2=" + cumulativeEnergyKwhTier2 + ", cumulativeEnergyKwhTier3="
				+ cumulativeEnergyKwhTier3 + ", cumulativeEnergyKwhTier4=" + cumulativeEnergyKwhTier4
				+ ", cumulativeEnergyKwhTier5=" + cumulativeEnergyKwhTier5 + ", cumulativeEnergyKwhTier6="
				+ cumulativeEnergyKwhTier6 + ", cumulativeEnergyKwhTier7=" + cumulativeEnergyKwhTier7
				+ ", cumulativeEnergyKwhTier8=" + cumulativeEnergyKwhTier8 + ", maximumDemandKva=" + maximumDemandKva
				+ ", maximumDemandKvaDatetime=" + maximumDemandKvaDatetime + ", maximumDemandKw=" + maximumDemandKw
				+ ", maximumDemandKwDatetime=" + maximumDemandKwDatetime + ", mdasDatetime=" + mdasDatetime + "]";
	}


    
}
