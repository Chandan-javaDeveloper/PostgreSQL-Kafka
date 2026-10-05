package com.jne.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public class InstantaneousDataThreePhaseId implements Serializable {

   

    @Column(name = "device_serial_number", nullable = false)
    private String deviceSerialNumber;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime Datetime;
    
    

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public LocalDateTime getDatetime() {
		return Datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		Datetime = datetime;
	}

	public InstantaneousDataThreePhaseId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InstantaneousDataThreePhaseId(LocalDate day, String deviceSerialNumber, LocalDateTime Datetime) {
		super();
		this.deviceSerialNumber = deviceSerialNumber;
		this.Datetime = Datetime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Datetime, deviceSerialNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstantaneousDataThreePhaseId other = (InstantaneousDataThreePhaseId) obj;
		return Objects.equals(Datetime, other.Datetime) && Objects.equals(deviceSerialNumber, other.deviceSerialNumber);
	}
	
	
}
