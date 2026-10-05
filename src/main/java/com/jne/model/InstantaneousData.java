package com.jne.model;



import java.time.LocalDateTime;

import com.jne.model_Id.InstantaneousDataId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "instantaneous_data")
public class InstantaneousData {

    @EmbeddedId
    private InstantaneousDataId id;

    @Column(name = "meter_datetime")
    private LocalDateTime meterDatetime;

    @Column(name = "owner_name", length = 255)
    private String ownerName;

    @Column(name = "timedrift")
    private Integer timedrift;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

	public InstantaneousDataId getId() {
		return id;
	}

	public void setId(InstantaneousDataId id) {
		this.id = id;
	}

	public LocalDateTime getMeterDatetime() {
		return meterDatetime;
	}

	public void setMeterDatetime(LocalDateTime meterDatetime) {
		this.meterDatetime = meterDatetime;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Integer getTimedrift() {
		return timedrift;
	}

	public void setTimedrift(Integer timedrift) {
		this.timedrift = timedrift;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}