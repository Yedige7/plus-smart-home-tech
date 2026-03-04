package ru.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapshotAggregator {

    // hubId -> snapshot
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (event == null) return Optional.empty();

        final String hubId = event.getHubId().toString();
        final String sensorId = event.getId().toString();
        final Instant eventTs = event.getTimestamp();

        SensorsSnapshotAvro snapshot = snapshots.get(hubId);
        if (snapshot == null) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(eventTs)
                    .setSensorsState(new HashMap<>())
                    .build();
            snapshots.put(hubId, snapshot);
        }

        Map<String, SensorStateAvro> stateMap = snapshot.getSensorsState();
        if (stateMap == null) {
            stateMap = new HashMap<>();
            snapshot.setSensorsState(stateMap);
        }

        SensorStateAvro oldState = stateMap.get(sensorId);

        Object payload = event.getPayload(); // union payload
        if (oldState != null) {
            Instant oldTs = oldState.getTimestamp();

            // событие старее последнего состояния
            if (oldTs.isBefore(eventTs)) {
                return Optional.empty();
            }

            // событие не меняет состояние (payload равен)
            Object oldData = oldState.getData();
            if (oldData != null && oldData.equals(payload)) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(eventTs)
                .setData(payload)
                .build();

        stateMap.put(sensorId, newState);
        snapshot.setTimestamp(eventTs);

        return Optional.of(snapshot);
    }
}
