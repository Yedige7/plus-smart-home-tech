package ru.practicum.dto.sensors;

import jakarta.validation.constraints.NotNull;

public class TemperatureSensorEventDto extends SensorEventDto {
    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer temperatureF;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Integer getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(Integer temperatureF) {
        this.temperatureF = temperatureF;
    }

    @Override
    public String toString() {
        return "TemperatureSensorEventDto{" +
                "id='" + getId() + '\'' +
                ", hubId='" + getHubId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", type=" + getType() +
                ", temperatureC=" + temperatureC +
                ", temperatureF=" + temperatureF +
                '}';
    }
}
