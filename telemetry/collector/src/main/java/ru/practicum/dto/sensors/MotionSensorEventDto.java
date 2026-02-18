package ru.practicum.dto.sensors;

import jakarta.validation.constraints.NotNull;

public class MotionSensorEventDto extends SensorEventDto {

    @NotNull
    private Integer linkQuality;

    @NotNull
    private Boolean motion;

    @NotNull
    private Integer voltage;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return "MotionSensorEventDto{" +
                "id='" + getId() + '\'' +
                ", hubId='" + getHubId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", type=" + getType() +
                ", linkQuality=" + linkQuality +
                ", motion=" + motion +
                ", voltage=" + voltage +
                '}';
    }
}
