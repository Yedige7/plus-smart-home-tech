package ru.practicum.mapper;

import ru.practicum.dto.hubs.*;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.time.Instant;

public final class HubEventProtoMapper {

    private HubEventProtoMapper() {
    }

    public static HubEventDto toDto(HubEventProto proto) {

        if (proto == null) throw new IllegalArgumentException("proto is null");

        Instant ts = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        return switch (proto.getPayloadCase()) {

            case DEVICE_ADDED -> {
                DeviceAddedEventDto dto = new DeviceAddedEventDto();
                fillBase(dto, proto, ts, HubEventType.DEVICE_ADDED);

                dto.setId(proto.getDeviceAdded().getId());

                dto.setDeviceType(
                        DeviceType.valueOf(
                                proto.getDeviceAdded().getType().name()
                        )
                );

                yield dto;
            }

            case DEVICE_REMOVED -> {
                DeviceRemovedEventDto dto = new DeviceRemovedEventDto();
                fillBase(dto, proto, ts, HubEventType.DEVICE_REMOVED);
                dto.setId(proto.getDeviceAdded().getId());

                yield dto;
            }

            case SCENARIO_ADDED -> {
                ScenarioAddedEventDto dto = new ScenarioAddedEventDto();
                fillBase(dto, proto, ts, HubEventType.SCENARIO_ADDED);

                dto.setName(proto.getScenarioAdded().getName());
                dto.setConditions(
                        proto.getScenarioAdded().getConditionList()
                                .stream()
                                .map(HubEventProtoMapper::mapCondition)
                                .toList()
                );

                dto.setActions(
                        proto.getScenarioAdded().getActionList()
                                .stream()
                                .map(HubEventProtoMapper::mapAction)
                                .toList()
                );

                yield dto;
            }

            case SCENARIO_REMOVED -> {
                ScenarioRemovedEventDto dto = new ScenarioRemovedEventDto();
                fillBase(dto, proto, ts, HubEventType.SCENARIO_REMOVED);

                dto.setName(proto.getScenarioRemoved().getName());

                yield dto;
            }

            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload not set");
        };
    }

    private static void fillBase(HubEventDto dto,
                                 HubEventProto proto,
                                 Instant ts,
                                 HubEventType type) {

        dto.setHubId(proto.getHubId());
        dto.setTimestamp(ts);
        dto.setType(type);
    }

    private static ScenarioConditionDto mapCondition(ScenarioConditionProto proto) {

        ScenarioConditionDto dto = new ScenarioConditionDto();

        dto.setSensorId(proto.getSensorId());
        dto.setType(ConditionType.valueOf(proto.getType().name()));
        dto.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

        if (proto.hasBoolValue()) {
            dto.setValue(proto.getBoolValue() ? 1 : 0);
        } else if (proto.hasIntValue()) {
            dto.setValue(proto.getIntValue());
        }

        return dto;
    }

    private static DeviceActionDto mapAction(DeviceActionProto proto) {

        DeviceActionDto dto = new DeviceActionDto();

        dto.setSensorId(proto.getSensorId());
        dto.setType(ActionType.valueOf(proto.getType().name()));

        if (proto.hasValue()) {
            dto.setValue(proto.getValue());
        }

        return dto;
    }
}