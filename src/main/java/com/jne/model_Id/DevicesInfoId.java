package com.jne.model_Id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DevicesInfoId implements Serializable {

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "crn")
    private String crn;

    public DevicesInfoId() {}

    public DevicesInfoId(String deviceSerialNumber, String crn) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.crn = crn;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DevicesInfoId)) return false;
        DevicesInfoId that = (DevicesInfoId) o;
        return Objects.equals(deviceSerialNumber, that.deviceSerialNumber) &&
               Objects.equals(crn, that.crn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceSerialNumber, crn);
    }
}
