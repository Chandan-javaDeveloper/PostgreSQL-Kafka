package com.jne.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "load_profile_data_threephase",
    indexes = {

        @Index(
            name = "idx_tp_device_interval_desc",
            columnList = "device_serial_number, interval_datetime"
        )
    }
)
public class LoadProfileThreePhase {

    @EmbeddedId
    private LoadProfileThreePhaseId id;

    @Column(name = "avg_signal_strength")
    private Double avgSignalStrength;

    @Column(name = "b_phase_active_power_kw")
    private Double bPhaseActivePowerKw;

    @Column(name = "block_energy_kvah_export")
    private Double blockEnergyKvahExport;

    @Column(name = "block_energy_kvah_import")
    private Double blockEnergyKvahImport;

    @Column(name = "block_energy_kwh_export")
    private Double blockEnergyKwhExport;

    @Column(name = "block_energy_kwh_import")
    private Double blockEnergyKwhImport;

    @Column(name = "cumulative_energy_kvarh_q1")
    private Double cumulativeEnergyKvarhQ1;

    @Column(name = "cumulative_energy_kvarh_q2")
    private Double cumulativeEnergyKvarhQ2;

    @Column(name = "cumulative_energy_kvarh_q3")
    private Double cumulativeEnergyKvarhQ3;

    @Column(name = "cumulative_energy_kvarh_q4")
    private Double cumulativeEnergyKvarhQ4;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "phase_current_l1")
    private Double phaseCurrentL1;

    @Column(name = "phase_current_l2")
    private Double phaseCurrentL2;

    @Column(name = "phase_current_l3")
    private Double phaseCurrentL3;

    @Column(name = "phase_voltage_l1")
    private Double phaseVoltageL1;

    @Column(name = "phase_voltage_l2")
    private Double phaseVoltageL2;

    @Column(name = "phase_voltage_l3")
    private Double phaseVoltageL3;

    @Column(name = "power_downtime_in_mins")
    private Integer powerDowntimeInMins;

    @Column(name = "r_phase_active_power_kw")
    private Double rPhaseActivePowerKw;

    @Column(name = "status_byte")
    private Double statusByte;

    @Column(name = "y_phase_active_power_kw")
    private Double yPhaseActivePowerKw;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    public LoadProfileThreePhase() {
    }

    public LoadProfileThreePhaseId getId() {
        return id;
    }

    public void setId(LoadProfileThreePhaseId id) {
        this.id = id;
    }

    public Double getAvgSignalStrength() {
        return avgSignalStrength;
    }

    public void setAvgSignalStrength(Double avgSignalStrength) {
        this.avgSignalStrength = avgSignalStrength;
    }

    public Double getbPhaseActivePowerKw() {
        return bPhaseActivePowerKw;
    }

    public void setbPhaseActivePowerKw(Double bPhaseActivePowerKw) {
        this.bPhaseActivePowerKw = bPhaseActivePowerKw;
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

    public LocalDateTime getMdasDatetime() {
        return mdasDatetime;
    }

    public void setMdasDatetime(LocalDateTime mdasDatetime) {
        this.mdasDatetime = mdasDatetime;
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

    public Double getPhaseVoltageL1() {
        return phaseVoltageL1;
    }

    public void setPhaseVoltageL1(Double phaseVoltageL1) {
        this.phaseVoltageL1 = phaseVoltageL1;
    }

    public Double getPhaseVoltageL2() {
        return phaseVoltageL2;
    }

    public void setPhaseVoltageL2(Double phaseVoltageL2) {
        this.phaseVoltageL2 = phaseVoltageL2;
    }

    public Double getPhaseVoltageL3() {
        return phaseVoltageL3;
    }

    public void setPhaseVoltageL3(Double phaseVoltageL3) {
        this.phaseVoltageL3 = phaseVoltageL3;
    }

    public Integer getPowerDowntimeInMins() {
        return powerDowntimeInMins;
    }

    public void setPowerDowntimeInMins(Integer powerDowntimeInMins) {
        this.powerDowntimeInMins = powerDowntimeInMins;
    }

    public Double getrPhaseActivePowerKw() {
        return rPhaseActivePowerKw;
    }

    public void setrPhaseActivePowerKw(Double rPhaseActivePowerKw) {
        this.rPhaseActivePowerKw = rPhaseActivePowerKw;
    }

    public Double getStatusByte() {
        return statusByte;
    }

    public void setStatusByte(Double statusByte) {
        this.statusByte = statusByte;
    }

    public Double getyPhaseActivePowerKw() {
        return yPhaseActivePowerKw;
    }

    public void setyPhaseActivePowerKw(Double yPhaseActivePowerKw) {
        this.yPhaseActivePowerKw = yPhaseActivePowerKw;
    }

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}
    
    
}