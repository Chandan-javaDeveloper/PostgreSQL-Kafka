package com.jne.model_Id;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class NamePlateId implements Serializable {

    @Column(name = "device_serial_number", length = 50, nullable = false)
    private String deviceSerialNumber;

    @Column(name = "mdas_datetime", nullable = false)
    private LocalDateTime mdasDatetime;

    public NamePlateId() {}

    public NamePlateId(String deviceSerialNumber, LocalDateTime mdasDatetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.mdasDatetime = mdasDatetime;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public LocalDateTime getMdasDatetime() {
        return mdasDatetime;
    }

    public void setMdasDatetime(LocalDateTime mdasDatetime) {
        this.mdasDatetime = mdasDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamePlateId)) return false;
        NamePlateId that = (NamePlateId) o;
        return Objects.equals(deviceSerialNumber, that.deviceSerialNumber) &&
               Objects.equals(mdasDatetime, that.mdasDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceSerialNumber, mdasDatetime);
    }
}