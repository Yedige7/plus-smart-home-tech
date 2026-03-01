package ru.practicum.mapper;

import ru.practicum.dto.sensors.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

public final class SensorEventProtoMapper {

    private SensorEventProtoMapper() {}

    public static SensorEventDto toDto(SensorEventProto proto) {
        if (proto == null) throw new IllegalArgumentException("proto is null");

        Instant ts = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        return switch (proto.getPayloadCase()) {

            case MOTION_SENSOR_EVENT -> {
                MotionSensorEventDto dto = new MotionSensorEventDto();
                fillBase(dto, proto, ts, SensorEventType.MOTION_SENSOR_EVENT);

                dto.setLinkQuality(proto.getMotionSensorEvent().getLinkQuality());
                dto.setMotion(proto.getMotionSensorEvent().getMotion());
                dto.setVoltage(proto.getMotionSensorEvent().getVoltage());

                yield dto;
            }

            case LIGHT_SENSOR_EVENT -> {
                LightSensorEventDto dto = new LightSensorEventDto();
                fillBase(dto, proto, ts, SensorEventType.LIGHT_SENSOR_EVENT);

                dto.setLinkQuality(proto.getLightSensorEvent().getLinkQuality());
                dto.setLuminosity(proto.getLightSensorEvent().getLuminosity());

                yield dto;
            }

            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEventDto dto = new SwitchSensorEventDto();
                fillBase(dto, proto, ts, SensorEventType.SWITCH_SENSOR_EVENT);

                dto.setState(proto.getSwitchSensorEvent().getState());

                yield dto;
            }

            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEventDto dto = new ClimateSensorEventDto();
                fillBase(dto, proto, ts, SensorEventType.CLIMATE_SENSOR_EVENT);

                dto.setTemperatureC(proto.getClimateSensorEvent().getTemperatureC());
                dto.setHumidity(proto.getClimateSensorEvent().getHumidity());
                dto.setCo2Level(proto.getClimateSensorEvent().getCo2Level());

                yield dto;
            }

            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEventDto dto = new TemperatureSensorEventDto();
                fillBase(dto, proto, ts, SensorEventType.TEMPERATURE_SENSOR_EVENT);

                dto.setTemperatureC(proto.getTemperatureSensorEvent().getTemperatureC());
                dto.setTemperatureF(proto.getTemperatureSensorEvent().getTemperatureF());

                yield dto;
            }

            case PAYLOAD_NOT_SET ->
                    throw new IllegalArgumentException("Payload not set");
        };
    }

    private static void fillBase(SensorEventDto dto, SensorEventProto proto, Instant ts, SensorEventType type) {
        dto.setId(proto.getId());
        dto.setHubId(proto.getHubId());
        dto.setTimestamp(ts);
        dto.setType(type);
    }
}