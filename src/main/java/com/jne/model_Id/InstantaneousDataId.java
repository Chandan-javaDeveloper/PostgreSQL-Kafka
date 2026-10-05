package com.jne.model_Id;



import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Embeddable
public class InstantaneousDataId implements Serializable {

    @Column(name = "mdas_datetime")
    private LocalDateTime mdasDatetime;

    @Column(name = "device_serial_number", length = 50)
    private String deviceSerialNumber;

	public LocalDateTime getMdasDatetime() {
		return mdasDatetime;
	}

	public void setMdasDatetime(LocalDateTime mdasDatetime) {
		this.mdasDatetime = mdasDatetime;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}
    
    
}