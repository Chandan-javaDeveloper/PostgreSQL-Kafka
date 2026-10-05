package com.jne.model_Id;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class InstantaneousPushSinglePhaseId implements Serializable {

    private String deviceSerialNumber;
    private LocalDateTime meterDatetime;

    public InstantaneousPushSinglePhaseId() {}

    public InstantaneousPushSinglePhaseId(String deviceSerialNumber, LocalDateTime meterDatetime) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.meterDatetime = meterDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstantaneousPushSinglePhaseId)) return false;
        InstantaneousPushSinglePhaseId that = (InstantaneousPushSinglePhaseId) o;
        return Objects.equals(deviceSerialNumber, that.deviceSerialNumber)
            && Objects.equals(meterDatetime, that.meterDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceSerialNumber, meterDatetime);
    }
}