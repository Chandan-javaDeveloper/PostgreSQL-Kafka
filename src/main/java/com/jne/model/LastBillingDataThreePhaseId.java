package com.jne.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LastBillingDataThreePhaseId implements Serializable {

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;


    @Column(name = "billing_datetime")
    private LocalDateTime billingDatetime;

    // Default constructor
    public LastBillingDataThreePhaseId() {}

    // Constructor
    public LastBillingDataThreePhaseId(String deviceSerialNumber,  LocalDateTime billingDatetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.billingDatetime = billingDatetime;
    }

    // Getters and Setters
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

   

    public LocalDateTime getBillingDatetime() {
        return billingDatetime;
    }

    public void setBillingDatetime(LocalDateTime billingDatetime) {
        this.billingDatetime = billingDatetime;
    }

	@Override
	public int hashCode() {
		return Objects.hash(billingDatetime, deviceSerialNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LastBillingDataThreePhaseId other = (LastBillingDataThreePhaseId) obj;
		return Objects.equals(billingDatetime, other.billingDatetime)
				&& Objects.equals(deviceSerialNumber, other.deviceSerialNumber);
	}

   
}
