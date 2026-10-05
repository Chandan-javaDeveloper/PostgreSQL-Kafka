package com.jne.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_load_profile_threephase")
public class DailyLoadProfileThreePhase {

    @EmbeddedId
    private DailyLoadProfileThreePhaseId id;

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

    @Column(name = "maximum_demand_kva")
    private Double maximumDemandKva;

    @Column(name = "maximum_demand_kva_datetime")
    private LocalDateTime maximumDemandKvaDatetime;

    @Column(name = "maximum_demand_kw")
    private Double maximumDemandKw;

    @Column(name = "maximum_demand_kw_datetime")
    private LocalDateTime maximumDemandKwDatetime;

    @Column(name = "maximum_demand_kw_export")
    private Double maximumDemandKwExport;

    @Column(name = "maximum_demand_kw_export_datetime")
    private LocalDateTime maximumDemandKwExportDatetime;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    
	public DailyLoadProfileThreePhase() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DailyLoadProfileThreePhase(DailyLoadProfileThreePhaseId id, Double cumulativeEnergyKvahExport,
			Double cumulativeEnergyKvahImport, Double cumulativeEnergyKvarhQ1, Double cumulativeEnergyKvarhQ2,
			Double cumulativeEnergyKvarhQ3, Double cumulativeEnergyKvarhQ4, Double cumulativeEnergyKwhExport,
			Double cumulativeEnergyKwhImport, Double maximumDemandKva, LocalDateTime maximumDemandKvaDatetime,
			Double maximumDemandKw, LocalDateTime maximumDemandKwDatetime, Double maximumDemandKwExport,
			LocalDateTime maximumDemandKwExportDatetime, LocalDateTime mdasDatetime) {
		super();
		this.id = id;
		this.cumulativeEnergyKvahExport = cumulativeEnergyKvahExport;
		this.cumulativeEnergyKvahImport = cumulativeEnergyKvahImport;
		this.cumulativeEnergyKvarhQ1 = cumulativeEnergyKvarhQ1;
		this.cumulativeEnergyKvarhQ2 = cumulativeEnergyKvarhQ2;
		this.cumulativeEnergyKvarhQ3 = cumulativeEnergyKvarhQ3;
		this.cumulativeEnergyKvarhQ4 = cumulativeEnergyKvarhQ4;
		this.cumulativeEnergyKwhExport = cumulativeEnergyKwhExport;
		this.cumulativeEnergyKwhImport = cumulativeEnergyKwhImport;
		this.maximumDemandKva = maximumDemandKva;
		this.maximumDemandKvaDatetime = maximumDemandKvaDatetime;
		this.maximumDemandKw = maximumDemandKw;
		this.maximumDemandKwDatetime = maximumDemandKwDatetime;
		this.maximumDemandKwExport = maximumDemandKwExport;
		this.maximumDemandKwExportDatetime = maximumDemandKwExportDatetime;
		this.mdasDatetime = mdasDatetime;
	}


	public DailyLoadProfileThreePhaseId getId() {
		return id;
	}


	public void setId(DailyLoadProfileThreePhaseId id) {
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


	public Double getMaximumDemandKwExport() {
		return maximumDemandKwExport;
	}


	public void setMaximumDemandKwExport(Double maximumDemandKwExport) {
		this.maximumDemandKwExport = maximumDemandKwExport;
	}


	public LocalDateTime getMaximumDemandKwExportDatetime() {
		return maximumDemandKwExportDatetime;
	}


	public void setMaximumDemandKwExportDatetime(LocalDateTime maximumDemandKwExportDatetime) {
		this.maximumDemandKwExportDatetime = maximumDemandKwExportDatetime;
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
		return "DailyLoadProfileThreePhase [id=" + id + ", cumulativeEnergyKvahExport=" + cumulativeEnergyKvahExport
				+ ", cumulativeEnergyKvahImport=" + cumulativeEnergyKvahImport + ", cumulativeEnergyKvarhQ1="
				+ cumulativeEnergyKvarhQ1 + ", cumulativeEnergyKvarhQ2=" + cumulativeEnergyKvarhQ2
				+ ", cumulativeEnergyKvarhQ3=" + cumulativeEnergyKvarhQ3 + ", cumulativeEnergyKvarhQ4="
				+ cumulativeEnergyKvarhQ4 + ", cumulativeEnergyKwhExport=" + cumulativeEnergyKwhExport
				+ ", cumulativeEnergyKwhImport=" + cumulativeEnergyKwhImport + ", maximumDemandKva=" + maximumDemandKva
				+ ", maximumDemandKvaDatetime=" + maximumDemandKvaDatetime + ", maximumDemandKw=" + maximumDemandKw
				+ ", maximumDemandKwDatetime=" + maximumDemandKwDatetime + ", maximumDemandKwExport="
				+ maximumDemandKwExport + ", maximumDemandKwExportDatetime=" + maximumDemandKwExportDatetime
				+ ", mdasDatetime=" + mdasDatetime + "]";
	}

	
}
