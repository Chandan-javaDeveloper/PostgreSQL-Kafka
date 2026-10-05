package com.jne.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_data_singlephase")
public class EventDataSinglePhase {

    @EmbeddedId
    private EventDataSinglePhaseId id;

    @Column(name = "cumulative_energy")
    private Double cumulativeEnergy;

    @Column(name = "current")
    private Double current;

    @Column(name = "event_category")
    private String eventCategory;

    @Column(name = "event_code")
    private Integer eventCode;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "power_factor")
    private Double powerFactor;

    @Column(name = "tamper_count")
    private Integer tamperCount;

    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "reading_type")
    private Integer ReadingType;

    // Getters and Setters

    public EventDataSinglePhaseId getId() {
        return id;
    }

    public void setId(EventDataSinglePhaseId id) {
        this.id = id;
    }

    public Double getCumulativeEnergy() {
        return cumulativeEnergy;
    }

    public void setCumulativeEnergy(Double cumulativeEnergy) {
        this.cumulativeEnergy = cumulativeEnergy;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
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

    public Double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(Double powerFactor) {
        this.powerFactor = powerFactor;
    }

    public Integer getTamperCount() {
        return tamperCount;
    }

    public void setTamperCount(Integer tamperCount) {
        this.tamperCount = tamperCount;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

	public Integer getReadingType() {
		return ReadingType;
	}

	public void setReadingType(Integer readingType) {
		ReadingType = readingType;
	}
    
    
}
