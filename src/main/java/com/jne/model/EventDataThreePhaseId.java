package com.jne.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EventDataThreePhaseId implements Serializable {

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;
    
    
    @Column(name = "event_datetime")
    private LocalDateTime eventDatetime;

    // Default constructor
    public EventDataThreePhaseId() {}

    // Constructor
    public EventDataThreePhaseId(String deviceSerialNumber,  LocalDateTime eventDatetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.eventDatetime = eventDatetime;
    }

    // Getters and Setters
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public LocalDateTime getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(LocalDateTime eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

	@Override
	public int hashCode() {
		return Objects.hash(deviceSerialNumber, eventDatetime);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventDataThreePhaseId other = (EventDataThreePhaseId) obj;
		return Objects.equals(deviceSerialNumber, other.deviceSerialNumber)
				&& Objects.equals(eventDatetime, other.eventDatetime);
	}

    
}
