package com.jne.model;



import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_data_threephase")
public class EventDataThreePhase {

    @EmbeddedId
    private EventDataThreePhaseId id;

    @Column(name = "cumulative_energy_kwh_export")
    private Double cumulativeEnergyKwhExport;

    @Column(name = "cumulative_energy_kwh_import")
    private Double cumulativeEnergyKwhImport;

    @Column(name = "cumulative_tamper_count")
    private Integer cumulativeTamperCount;

    @Column(name = "current_ib")
    private Double currentIb;

    @Column(name = "current_ir")
    private Double currentIr;

    @Column(name = "current_iy")
    private Double currentIy;

    @Column(name = "event_category")
    private String eventCategory;

    @Column(name = "event_code")
    private Integer eventCode;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "power_factor_b_phase")
    private Double powerFactorBPhase;

    @Column(name = "power_factor_r_phase")
    private Double powerFactorRPhase;

    @Column(name = "power_factor_y_phase")
    private Double powerFactorYPhase;

    @Column(name = "voltage_vbn")
    private Double voltageVbn;

    @Column(name = "voltage_vrn")
    private Double voltageVrn;

    @Column(name = "voltage_vyn")
    private Double voltageVyn;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    // Getters and Setters
    public EventDataThreePhaseId getId() {
        return id;
    }

    public void setId(EventDataThreePhaseId id) {
        this.id = id;
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

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Integer getEventCode() {
        return eventCode;
    }

    public void setEventCode(Integer eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getMdasDatetime() {
        return mdasDatetime;
    }

    public void setMdasDatetime(LocalDateTime mdasDatetime) {
        this.mdasDatetime = mdasDatetime;
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

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}
    
    
}
