package ru.practicum.dto.sensors;

import jakarta.validation.constraints.NotNull;

public class ClimateSensorEventDto extends SensorEventDto {
    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer humidity;

    @NotNull
    private Integer co2Level;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getCo2Level() {
        return co2Level;
    }

    public void setCo2Level(Integer co2Level) {
        this.co2Level = co2Level;
    }

    @Override
    public String toString() {
        return "ClimateSensorEventDto{" +
                "id='" + getId() + '\'' +
                ", hubId='" + getHubId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", type=" + getType() +
                ", temperatureC=" + temperatureC +
                ", humidity=" + humidity +
                ", co2Level=" + co2Level +
                '}';
    }
}
