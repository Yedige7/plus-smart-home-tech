package ru.practicum.mapper;

import ru.practicum.dto.hubs.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

public final class HubEventMapper {

    private HubEventMapper() {
    }

    public static HubEventAvro toAvro(HubEventDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto is null");

        Instant ts = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();

        Object payload = switch (dto.getType()) {
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEventDto) dto);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEventDto) dto);
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEventDto) dto);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEventDto) dto);
        };

        // В Avro schema поле hub_id, но в Java почти наверняка будет setHubId(...)
        return HubEventAvro.newBuilder().setHubId(dto.getHubId()).setTimestamp(ts)      // Instant
                .setPayload(payload)   // union
                .build();
    }

    private static DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEventDto dto) {
        DeviceTypeAvro avroType = DeviceTypeAvro.valueOf(dto.getDeviceType().name());

        return DeviceAddedEventAvro.newBuilder().setId(dto.getId()).setType(avroType).build();
    }

    private static DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEventDto dto) {
        return DeviceRemovedEventAvro.newBuilder().setId(dto.getId()).build();
    }

    private static ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEventDto dto) {
        List<ScenarioConditionAvro> conditions = dto.getConditions().stream().map(HubEventMapper::mapCondition).toList();

        List<DeviceActionAvro> actions = dto.getActions().stream().map(HubEventMapper::mapAction).toList();

        return ScenarioAddedEventAvro.newBuilder().setName(dto.getName()).setConditions(conditions).setActions(actions).build();
    }

    private static ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEventDto dto) {
        return ScenarioRemovedEventAvro.newBuilder().setName(dto.getName()).build();
    }

    private static ScenarioConditionAvro mapCondition(ScenarioConditionDto dto) {
        ConditionTypeAvro type = ConditionTypeAvro.valueOf(dto.getType().name());
        ConditionOperationAvro op = ConditionOperationAvro.valueOf(dto.getOperation().name());

        ScenarioConditionAvro.Builder b = ScenarioConditionAvro.newBuilder().setSensorId(dto.getSensorId()).setType(type).setOperation(op);

        if (dto.getValue() == null) {
            b.setValue(null);
        } else {
            b.setValue(dto.getValue());
        }

        return b.build();
    }

    private static DeviceActionAvro mapAction(DeviceActionDto dto) {
        ActionTypeAvro type = ActionTypeAvro.valueOf(dto.getType().name());

        DeviceActionAvro.Builder b = DeviceActionAvro.newBuilder().setSensorId(dto.getSensorId()).setType(type);

        if (dto.getValue() == null) {
            b.setValue(null);
        } else {
            b.setValue(dto.getValue());
        }

        return b.build();
    }
}
