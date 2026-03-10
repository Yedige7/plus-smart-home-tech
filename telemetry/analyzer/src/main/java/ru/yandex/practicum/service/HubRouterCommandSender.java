package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioActionLink;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;

@Slf4j
@Service
public class HubRouterCommandSender {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public void send(SensorsSnapshotAvro snapshot,
                     Scenario scenario,
                     ScenarioActionLink link) {

        try {
            ActionTypeProto actionType =
                    ActionTypeProto.valueOf(link.getAction().getType().toUpperCase());

            DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                    .setSensorId(link.getSensor().getId())
                    .setType(actionType);

            if (link.getAction().getValue() != null) {
                actionBuilder.setValue(link.getAction().getValue());
            }

            DeviceActionProto action = actionBuilder.build();

            Instant now = Instant.now();
            Timestamp ts = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            DeviceActionRequest req = DeviceActionRequest.newBuilder()
                    .setHubId(snapshot.getHubId().toString())
                    .setScenarioName(scenario.getName())
                    .setAction(action)
                    .setTimestamp(ts)
                    .build();
            log.info("gRPC request payload: {}", req);

            log.info("Sending gRPC action: hubId={}, scenarioName={}, targetId={}, actionType={}, value={}",
                    snapshot.getHubId(),
                    scenario.getName(),
                    link.getSensor().getId(),
                    actionType,
                    link.getAction().getValue());

            client.handleDeviceAction(req);

            log.info("gRPC action sent successfully");

        } catch (Exception e) {
            log.error("Failed to send device action", e);
        }
    }
}