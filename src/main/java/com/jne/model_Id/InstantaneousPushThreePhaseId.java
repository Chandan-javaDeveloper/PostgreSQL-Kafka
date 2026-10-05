package com.jne.model_Id;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class InstantaneousPushThreePhaseId implements Serializable {

    private String deviceSerialNumber;
    private LocalDateTime meterDatetime;

    public InstantaneousPushThreePhaseId() {}

    public InstantaneousPushThreePhaseId(String deviceSerialNumber, LocalDateTime meterDatetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.meterDatetime = meterDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstantaneousPushThreePhaseId)) return false;
        InstantaneousPushThreePhaseId that = (InstantaneousPushThreePhaseId) o;
        return Objects.equals(deviceSerialNumber, that.deviceSerialNumber)
            && Objects.equals(meterDatetime, that.meterDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceSerialNumber, meterDatetime);
    }
}