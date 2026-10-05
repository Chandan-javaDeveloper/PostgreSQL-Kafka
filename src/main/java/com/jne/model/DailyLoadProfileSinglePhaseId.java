package com.jne.model;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DailyLoadProfileSinglePhaseId implements Serializable {


    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    public DailyLoadProfileSinglePhaseId() {}

    public DailyLoadProfileSinglePhaseId(LocalDate day, String deviceSerialNumber, LocalDateTime datetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.datetime = datetime;
    }

    

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

	@Override
	public int hashCode() {
		return Objects.hash(datetime, deviceSerialNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyLoadProfileSinglePhaseId other = (DailyLoadProfileSinglePhaseId) obj;
		return Objects.equals(datetime, other.datetime) && Objects.equals(deviceSerialNumber, other.deviceSerialNumber);
	}

    // IMPORTANT for composite key
   
    
}
