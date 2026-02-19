package ru.practicum.mapper;

import ru.practicum.dto.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;


import java.time.Instant;

public final class SensorEventMapper {

    private SensorEventMapper() {}

    public static SensorEventAvro toAvro(SensorEventDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto is null");

        Instant ts = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();

        Object payload = switch (dto.getType()) {
            case LIGHT_SENSOR_EVENT -> mapLight((LightSensorEventDto) dto);
            case MOTION_SENSOR_EVENT -> mapMotion((MotionSensorEventDto) dto);
            case SWITCH_SENSOR_EVENT -> mapSwitch((SwitchSensorEventDto) dto);
            case CLIMATE_SENSOR_EVENT -> mapClimate((ClimateSensorEventDto) dto);
            case TEMPERATURE_SENSOR_EVENT -> mapTemperature((TemperatureSensorEventDto) dto, ts);
        };

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(ts)     // ВАЖНО: Instant (logicalType timestamp-millis)
                .setPayload(payload)  // union
                .build();
    }

    private static LightSensorAvro mapLight(LightSensorEventDto dto) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(requireInt(dto.getLinkQuality(), "linkQuality"))
                .setLuminosity(requireInt(dto.getLuminosity(), "luminosity"))
                .build();
    }

    private static MotionSensorAvro mapMotion(MotionSensorEventDto dto) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(requireInt(dto.getLinkQuality(), "linkQuality"))
                .setMotion(requireBool(dto.getMotion(), "motion"))
                .setVoltage(requireInt(dto.getVoltage(), "voltage"))
                .build();
    }

    private static SwitchSensorAvro mapSwitch(SwitchSensorEventDto dto) {
        return SwitchSensorAvro.newBuilder()
                .setState(requireBool(dto.getState(), "state"))
                .build();
    }

    private static ClimateSensorAvro mapClimate(ClimateSensorEventDto dto) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(requireInt(dto.getTemperatureC(), "temperatureC"))
                .setHumidity(requireInt(dto.getHumidity(), "humidity"))
                .setCo2Level(requireInt(dto.getCo2Level(), "co2Level"))
                .build();
    }

    private static TemperatureSensorAvro mapTemperature(TemperatureSensorEventDto dto, Instant ts) {
        return TemperatureSensorAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(ts) // Instant
                .setTemperatureC(requireInt(dto.getTemperatureC(), "temperatureC"))
                .setTemperatureF(requireInt(dto.getTemperatureF(), "temperatureF"))
                .build();
    }

    private static int requireInt(Integer v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " is null");
        return v;
    }

    private static boolean requireBool(Boolean v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " is null");
        return v;
    }
}
