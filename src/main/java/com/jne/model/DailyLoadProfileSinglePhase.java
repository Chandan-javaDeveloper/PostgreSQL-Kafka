package com.jne.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_load_profile_singlephase")
public class DailyLoadProfileSinglePhase {

    @EmbeddedId
    private DailyLoadProfileSinglePhaseId id;

    @Column(name = "cumulative_energy_kvah_export")
    private Double cumulativeEnergyKvahExport;

    @Column(name = "cumulative_energy_kvah_import")
    private Double cumulativeEnergyKvahImport;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;
    
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    public DailyLoadProfileSinglePhase() {}

    // getters & setters

    public DailyLoadProfileSinglePhaseId getId() {
        return id;
    }

    public void setId(DailyLoadProfileSinglePhaseId id) {
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
    
    
}
