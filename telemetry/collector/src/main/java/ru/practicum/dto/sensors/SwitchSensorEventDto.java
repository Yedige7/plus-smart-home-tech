package ru.practicum.dto.sensors;

import jakarta.validation.constraints.NotNull;

public class SwitchSensorEventDto extends SensorEventDto {

    @NotNull
    private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SwitchSensorEventDto{" +
                "id='" + getId() + '\'' +
                ", hubId='" + getHubId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", type=" + getType() +
                ", state=" + state +
                '}';
    }
}
