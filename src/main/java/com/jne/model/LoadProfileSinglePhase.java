package com.jne.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "load_profile_data_singlephase",
    indexes = {

        @Index(
            name = "idx_sp_device_interval",
            columnList = "device_serial_number, interval_datetime"
        )
    }
)
public class LoadProfileSinglePhase {

    @EmbeddedId
    private LoadProfileSinglePhaseId id;

    @Column(name = "average_current")
    private Double averageCurrent;

    @Column(name = "average_voltage")
    private Double averageVoltage;

    @Column(name = "block_energy_kvah_export")
    private Double blockEnergyKvahExport;

    @Column(name = "block_energy_kvah_import")
    private Double blockEnergyKvahImport;

    @Column(name = "block_energy_kwh_export")
    private Double blockEnergyKwhExport;

    @Column(name = "block_energy_kwh_import")
    private Double blockEnergyKwhImport;

    @Column(name = "frequency")
    private Double frequency;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "meter_datetime")
    private LocalDateTime meterDatetime;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    public LoadProfileSinglePhase() {
    }

    public LoadProfileSinglePhaseId getId() {
        return id;
    }

    public void setId(LoadProfileSinglePhaseId id) {
        this.id = id;
    }

    public Double getAverageCurrent() {
        return averageCurrent;
    }

    public void setAverageCurrent(Double averageCurrent) {
        this.averageCurrent = averageCurrent;
    }

    public Double getAverageVoltage() {
        return averageVoltage;
    }

    public void setAverageVoltage(Double averageVoltage) {
        this.averageVoltage = averageVoltage;
    }

    public Double getBlockEnergyKvahExport() {
        return blockEnergyKvahExport;
    }

    public void setBlockEnergyKvahExport(Double blockEnergyKvahExport) {
        this.blockEnergyKvahExport = blockEnergyKvahExport;
    }

    public Double getBlockEnergyKvahImport() {
        return blockEnergyKvahImport;
    }

    public void setBlockEnergyKvahImport(Double blockEnergyKvahImport) {
        this.blockEnergyKvahImport = blockEnergyKvahImport;
    }

    public Double getBlockEnergyKwhExport() {
        return blockEnergyKwhExport;
    }

    public void setBlockEnergyKwhExport(Double blockEnergyKwhExport) {
        this.blockEnergyKwhExport = blockEnergyKwhExport;
    }

    public Double getBlockEnergyKwhImport() {
        return blockEnergyKwhImport;
    }

    public void setBlockEnergyKwhImport(Double blockEnergyKwhImport) {
        this.blockEnergyKwhImport = blockEnergyKwhImport;
    }

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getMdasDatetime() {
        return mdasDatetime;
    }

    public void setMdasDatetime(LocalDateTime mdasDatetime) {
        this.mdasDatetime = mdasDatetime;
    }

    public LocalDateTime getMeterDatetime() {
        return meterDatetime;
    }

    public void setMeterDatetime(LocalDateTime meterDatetime) {
        this.meterDatetime = meterDatetime;
    }

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}
    
    
}