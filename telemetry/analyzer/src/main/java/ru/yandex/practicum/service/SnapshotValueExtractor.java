package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.OptionalInt;

public final class SnapshotValueExtractor {

    private SnapshotValueExtractor() {
    }

    public static OptionalInt extract(SensorsSnapshotAvro snapshot, String sensorId, String conditionType) {
        if (snapshot == null || snapshot.getSensorsState() == null) return OptionalInt.empty();

        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
        if (state == null || state.getData() == null) return OptionalInt.empty();

        ConditionTypeProto type = ConditionTypeProto.valueOf(conditionType);

        Object data = state.getData();

        switch (type) {
            case MOTION -> {
                if (data instanceof MotionSensorAvro m) {
                    return OptionalInt.of(Boolean.TRUE.equals(m.getMotion()) ? 1 : 0);
                }
                return OptionalInt.empty();
            }
            case LUMINOSITY -> {
                if (data instanceof LightSensorAvro l) {
                    return OptionalInt.of(l.getLuminosity());
                }
                return OptionalInt.empty();
            }
            case SWITCH -> {
                if (data instanceof SwitchSensorAvro s) {
                    return OptionalInt.of(Boolean.TRUE.equals(s.getState()) ? 1 : 0);
                }
                return OptionalInt.empty();
            }
            case TEMPERATURE -> {
                if (data instanceof TemperatureSensorAvro t) {
                    return OptionalInt.of(t.getTemperatureC());
                }
                if (data instanceof ClimateSensorAvro c) {
                    return OptionalInt.of(c.getTemperatureC());
                }
                return OptionalInt.empty();
            }
            case CO2LEVEL -> {
                if (data instanceof ClimateSensorAvro c) {
                    return OptionalInt.of(c.getCo2Level());
                }
                return OptionalInt.empty();
            }
            case HUMIDITY -> {
                if (data instanceof ClimateSensorAvro c) {
                    return OptionalInt.of(c.getHumidity());
                }
                return OptionalInt.empty();
            }
            default -> {
                return OptionalInt.empty();
            }
        }
    }
}
