package com.jne.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "last_billing_data_threephase")
public class LastBillingDataThreePhase {

    @EmbeddedId
    private LastBillingDataThreePhaseId id;

    // Cumulative energy
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

    // Maximum demand KVA
    @Column(name = "maximum_demand_kva")
    private Double maximumDemandKva;
    @Column(name = "maximum_demand_kva_date")
    private LocalDateTime maximumDemandKvaDate;
    @Column(name = "maximum_demand_kva_tier1")
    private Double maximumDemandKvaTier1;
    @Column(name = "maximum_demand_kva_tier1_date")
    private LocalDateTime maximumDemandKvaTier1Date;
    @Column(name = "maximum_demand_kva_tier2")
    private Double maximumDemandKvaTier2;
    @Column(name = "maximum_demand_kva_tier2_date")
    private LocalDateTime maximumDemandKvaTier2Date;
    @Column(name = "maximum_demand_kva_tier3")
    private Double maximumDemandKvaTier3;
    @Column(name = "maximum_demand_kva_tier3_date")
    private LocalDateTime maximumDemandKvaTier3Date;
    @Column(name = "maximum_demand_kva_tier4")
    private Double maximumDemandKvaTier4;
    @Column(name = "maximum_demand_kva_tier4_date")
    private LocalDateTime maximumDemandKvaTier4Date;
    @Column(name = "maximum_demand_kva_tier5")
    private Double maximumDemandKvaTier5;
    @Column(name = "maximum_demand_kva_tier5_date")
    private LocalDateTime maximumDemandKvaTier5Date;
    @Column(name = "maximum_demand_kva_tier6")
    private Double maximumDemandKvaTier6;
    @Column(name = "maximum_demand_kva_tier6_date")
    private LocalDateTime maximumDemandKvaTier6Date;
    @Column(name = "maximum_demand_kva_tier7")
    private Double maximumDemandKvaTier7;
    @Column(name = "maximum_demand_kva_tier7_date")
    private LocalDateTime maximumDemandKvaTier7Date;
    @Column(name = "maximum_demand_kva_tier8")
    private Double maximumDemandKvaTier8;
    @Column(name = "maximum_demand_kva_tier8_date")
    private LocalDateTime maximumDemandKvaTier8Date;

    // Maximum demand KW
    @Column(name = "maximum_demand_kw")
    private Double maximumDemandKw;
    @Column(name = "maximum_demand_kw_date")
    private LocalDateTime maximumDemandKwDate;
    @Column(name = "maximum_demand_kw_tier1")
    private Double maximumDemandKwTier1;
    @Column(name = "maximum_demand_kw_tier1_date")
    private LocalDateTime maximumDemandKwTier1Date;
    @Column(name = "maximum_demand_kw_tier2")
    private Double maximumDemandKwTier2;
    @Column(name = "maximum_demand_kw_tier2_date")
    private LocalDateTime maximumDemandKwTier2Date;
    @Column(name = "maximum_demand_kw_tier3")
    private Double maximumDemandKwTier3;
    @Column(name = "maximum_demand_kw_tier3_date")
    private LocalDateTime maximumDemandKwTier3Date;
    @Column(name = "maximum_demand_kw_tier4")
    private Double maximumDemandKwTier4;
    @Column(name = "maximum_demand_kw_tier4_date")
    private LocalDateTime maximumDemandKwTier4Date;
    @Column(name = "maximum_demand_kw_tier5")
    private Double maximumDemandKwTier5;
    @Column(name = "maximum_demand_kw_tier5_date")
    private LocalDateTime maximumDemandKwTier5Date;
    @Column(name = "maximum_demand_kw_tier6")
    private Double maximumDemandKwTier6;
    @Column(name = "maximum_demand_kw_tier6_date")
    private LocalDateTime maximumDemandKwTier6Date;
    @Column(name = "maximum_demand_kw_tier7")
    private Double maximumDemandKwTier7;
    @Column(name = "maximum_demand_kw_tier7_date")
    private LocalDateTime maximumDemandKwTier7Date;
    @Column(name = "maximum_demand_kw_tier8")
    private Double maximumDemandKwTier8;
    @Column(name = "maximum_demand_kw_tier8_date")
    private LocalDateTime maximumDemandKwTier8Date;

    // Others
    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;
   
    @Column(name = "power_on_duration_mins")
    private Integer powerOnDurationMins;
    @Column(name = "system_power_factor_billing_period")
    private Double systemPowerFactorBillingPeriod;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

	public LastBillingDataThreePhaseId getId() {
		return id;
	}

	public void setId(LastBillingDataThreePhaseId id) {
		this.id = id;
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
	
	

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
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

	public LocalDateTime getMaximumDemandKvaDate() {
		return maximumDemandKvaDate;
	}

	public void setMaximumDemandKvaDate(LocalDateTime maximumDemandKvaDate) {
		this.maximumDemandKvaDate = maximumDemandKvaDate;
	}

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public Double getSystemPowerFactorBillingPeriod() {
		return systemPowerFactorBillingPeriod;
	}

	public void setSystemPowerFactorBillingPeriod(Double systemPowerFactorBillingPeriod) {
		this.systemPowerFactorBillingPeriod = systemPowerFactorBillingPeriod;
	}
	
	

	public Double getMaximumDemandKvaTier1() {
		return maximumDemandKvaTier1;
	}

	public void setMaximumDemandKvaTier1(Double maximumDemandKvaTier1) {
		this.maximumDemandKvaTier1 = maximumDemandKvaTier1;
	}

	public LocalDateTime getMaximumDemandKvaTier1Date() {
		return maximumDemandKvaTier1Date;
	}

	public void setMaximumDemandKvaTier1Date(LocalDateTime maximumDemandKvaTier1Date) {
		this.maximumDemandKvaTier1Date = maximumDemandKvaTier1Date;
	}

	public Double getMaximumDemandKvaTier2() {
		return maximumDemandKvaTier2;
	}

	public void setMaximumDemandKvaTier2(Double maximumDemandKvaTier2) {
		this.maximumDemandKvaTier2 = maximumDemandKvaTier2;
	}

	public LocalDateTime getMaximumDemandKvaTier2Date() {
		return maximumDemandKvaTier2Date;
	}

	public void setMaximumDemandKvaTier2Date(LocalDateTime maximumDemandKvaTier2Date) {
		this.maximumDemandKvaTier2Date = maximumDemandKvaTier2Date;
	}

	public Double getMaximumDemandKvaTier3() {
		return maximumDemandKvaTier3;
	}

	public void setMaximumDemandKvaTier3(Double maximumDemandKvaTier3) {
		this.maximumDemandKvaTier3 = maximumDemandKvaTier3;
	}

	public LocalDateTime getMaximumDemandKvaTier3Date() {
		return maximumDemandKvaTier3Date;
	}

	public void setMaximumDemandKvaTier3Date(LocalDateTime maximumDemandKvaTier3Date) {
		this.maximumDemandKvaTier3Date = maximumDemandKvaTier3Date;
	}

	public Double getMaximumDemandKvaTier4() {
		return maximumDemandKvaTier4;
	}

	public void setMaximumDemandKvaTier4(Double maximumDemandKvaTier4) {
		this.maximumDemandKvaTier4 = maximumDemandKvaTier4;
	}

	public LocalDateTime getMaximumDemandKvaTier4Date() {
		return maximumDemandKvaTier4Date;
	}

	public void setMaximumDemandKvaTier4Date(LocalDateTime maximumDemandKvaTier4Date) {
		this.maximumDemandKvaTier4Date = maximumDemandKvaTier4Date;
	}

	public Double getMaximumDemandKvaTier5() {
		return maximumDemandKvaTier5;
	}

	public void setMaximumDemandKvaTier5(Double maximumDemandKvaTier5) {
		this.maximumDemandKvaTier5 = maximumDemandKvaTier5;
	}

	public LocalDateTime getMaximumDemandKvaTier5Date() {
		return maximumDemandKvaTier5Date;
	}

	public void setMaximumDemandKvaTier5Date(LocalDateTime maximumDemandKvaTier5Date) {
		this.maximumDemandKvaTier5Date = maximumDemandKvaTier5Date;
	}

	public Double getMaximumDemandKvaTier6() {
		return maximumDemandKvaTier6;
	}

	public void setMaximumDemandKvaTier6(Double maximumDemandKvaTier6) {
		this.maximumDemandKvaTier6 = maximumDemandKvaTier6;
	}

	public LocalDateTime getMaximumDemandKvaTier6Date() {
		return maximumDemandKvaTier6Date;
	}

	public void setMaximumDemandKvaTier6Date(LocalDateTime maximumDemandKvaTier6Date) {
		this.maximumDemandKvaTier6Date = maximumDemandKvaTier6Date;
	}

	public Double getMaximumDemandKvaTier7() {
		return maximumDemandKvaTier7;
	}

	public void setMaximumDemandKvaTier7(Double maximumDemandKvaTier7) {
		this.maximumDemandKvaTier7 = maximumDemandKvaTier7;
	}

	public LocalDateTime getMaximumDemandKvaTier7Date() {
		return maximumDemandKvaTier7Date;
	}

	public void setMaximumDemandKvaTier7Date(LocalDateTime maximumDemandKvaTier7Date) {
		this.maximumDemandKvaTier7Date = maximumDemandKvaTier7Date;
	}

	public Double getMaximumDemandKvaTier8() {
		return maximumDemandKvaTier8;
	}

	public void setMaximumDemandKvaTier8(Double maximumDemandKvaTier8) {
		this.maximumDemandKvaTier8 = maximumDemandKvaTier8;
	}

	public LocalDateTime getMaximumDemandKvaTier8Date() {
		return maximumDemandKvaTier8Date;
	}

	public void setMaximumDemandKvaTier8Date(LocalDateTime maximumDemandKvaTier8Date) {
		this.maximumDemandKvaTier8Date = maximumDemandKvaTier8Date;
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

	public Double getMaximumDemandKwTier1() {
		return maximumDemandKwTier1;
	}

	public void setMaximumDemandKwTier1(Double maximumDemandKwTier1) {
		this.maximumDemandKwTier1 = maximumDemandKwTier1;
	}

	public LocalDateTime getMaximumDemandKwTier1Date() {
		return maximumDemandKwTier1Date;
	}

	public void setMaximumDemandKwTier1Date(LocalDateTime maximumDemandKwTier1Date) {
		this.maximumDemandKwTier1Date = maximumDemandKwTier1Date;
	}

	public Double getMaximumDemandKwTier2() {
		return maximumDemandKwTier2;
	}

	public void setMaximumDemandKwTier2(Double maximumDemandKwTier2) {
		this.maximumDemandKwTier2 = maximumDemandKwTier2;
	}

	public LocalDateTime getMaximumDemandKwTier2Date() {
		return maximumDemandKwTier2Date;
	}

	public void setMaximumDemandKwTier2Date(LocalDateTime maximumDemandKwTier2Date) {
		this.maximumDemandKwTier2Date = maximumDemandKwTier2Date;
	}

	public Double getMaximumDemandKwTier3() {
		return maximumDemandKwTier3;
	}

	public void setMaximumDemandKwTier3(Double maximumDemandKwTier3) {
		this.maximumDemandKwTier3 = maximumDemandKwTier3;
	}

	public LocalDateTime getMaximumDemandKwTier3Date() {
		return maximumDemandKwTier3Date;
	}

	public void setMaximumDemandKwTier3Date(LocalDateTime maximumDemandKwTier3Date) {
		this.maximumDemandKwTier3Date = maximumDemandKwTier3Date;
	}

	public Double getMaximumDemandKwTier4() {
		return maximumDemandKwTier4;
	}

	public void setMaximumDemandKwTier4(Double maximumDemandKwTier4) {
		this.maximumDemandKwTier4 = maximumDemandKwTier4;
	}

	public LocalDateTime getMaximumDemandKwTier4Date() {
		return maximumDemandKwTier4Date;
	}

	public void setMaximumDemandKwTier4Date(LocalDateTime maximumDemandKwTier4Date) {
		this.maximumDemandKwTier4Date = maximumDemandKwTier4Date;
	}

	public Double getMaximumDemandKwTier5() {
		return maximumDemandKwTier5;
	}

	public void setMaximumDemandKwTier5(Double maximumDemandKwTier5) {
		this.maximumDemandKwTier5 = maximumDemandKwTier5;
	}

	public LocalDateTime getMaximumDemandKwTier5Date() {
		return maximumDemandKwTier5Date;
	}

	public void setMaximumDemandKwTier5Date(LocalDateTime maximumDemandKwTier5Date) {
		this.maximumDemandKwTier5Date = maximumDemandKwTier5Date;
	}

	public Double getMaximumDemandKwTier6() {
		return maximumDemandKwTier6;
	}

	public void setMaximumDemandKwTier6(Double maximumDemandKwTier6) {
		this.maximumDemandKwTier6 = maximumDemandKwTier6;
	}

	public LocalDateTime getMaximumDemandKwTier6Date() {
		return maximumDemandKwTier6Date;
	}

	public void setMaximumDemandKwTier6Date(LocalDateTime maximumDemandKwTier6Date) {
		this.maximumDemandKwTier6Date = maximumDemandKwTier6Date;
	}

	public Double getMaximumDemandKwTier7() {
		return maximumDemandKwTier7;
	}

	public void setMaximumDemandKwTier7(Double maximumDemandKwTier7) {
		this.maximumDemandKwTier7 = maximumDemandKwTier7;
	}

	public LocalDateTime getMaximumDemandKwTier7Date() {
		return maximumDemandKwTier7Date;
	}

	public void setMaximumDemandKwTier7Date(LocalDateTime maximumDemandKwTier7Date) {
		this.maximumDemandKwTier7Date = maximumDemandKwTier7Date;
	}

	public Double getMaximumDemandKwTier8() {
		return maximumDemandKwTier8;
	}

	public void setMaximumDemandKwTier8(Double maximumDemandKwTier8) {
		this.maximumDemandKwTier8 = maximumDemandKwTier8;
	}

	public LocalDateTime getMaximumDemandKwTier8Date() {
		return maximumDemandKwTier8Date;
	}

	public void setMaximumDemandKwTier8Date(LocalDateTime maximumDemandKwTier8Date) {
		this.maximumDemandKwTier8Date = maximumDemandKwTier8Date;
	}


	public Integer getPowerOnDurationMins() {
		return powerOnDurationMins;
	}

	public void setPowerOnDurationMins(Integer powerOnDurationMins) {
		this.powerOnDurationMins = powerOnDurationMins;
	}

	
   
}
