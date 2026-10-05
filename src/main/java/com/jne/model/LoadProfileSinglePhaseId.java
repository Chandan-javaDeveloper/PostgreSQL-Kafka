package com.jne.model;



import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class LoadProfileSinglePhaseId implements Serializable {

    

    @Column(name = "device_serial_number", nullable = false)
    private String deviceSerialNumber;

    @Column(name = "interval_datetime", nullable = false)
    private LocalDateTime intervalDatetime;

    public LoadProfileSinglePhaseId() {}

    

    public String getDeviceSerialNumber() { return deviceSerialNumber; }
    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public LocalDateTime getIntervalDatetime() { return intervalDatetime; }
    public void setIntervalDatetime(LocalDateTime intervalDatetime) {
        this.intervalDatetime = intervalDatetime;
    }



	@Override
	public int hashCode() {
		return Objects.hash(deviceSerialNumber, intervalDatetime);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadProfileSinglePhaseId other = (LoadProfileSinglePhaseId) obj;
		return Objects.equals(deviceSerialNumber, other.deviceSerialNumber)
				&& Objects.equals(intervalDatetime, other.intervalDatetime);
	}
    
    

}
