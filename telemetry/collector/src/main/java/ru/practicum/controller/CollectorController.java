package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.avro.AvroBinarySerializer;
import ru.practicum.kafka.KafkaPublisher;
import ru.practicum.mapper.HubEventMapper;
import ru.practicum.mapper.HubEventProtoMapper;
import ru.practicum.mapper.SensorEventMapper;
import ru.practicum.mapper.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@GrpcService
@RequiredArgsConstructor
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final KafkaPublisher kafkaPublisher;
    private final AvroBinarySerializer avroSerializer;

    @Override
    public void collectSensorEvent(SensorEventProto request,
                                   StreamObserver<Empty> responseObserver) {

        var dto = SensorEventProtoMapper.toDto(request);
        var avro = SensorEventMapper.toAvro(dto);
        byte[] bytes = avroSerializer.toBytes(avro);

        kafkaPublisher.sendAndWait(
                "telemetry.sensors.v1",
                request.getHubId(),
                bytes
        );

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void collectHubEvent(HubEventProto request,
                                StreamObserver<Empty> responseObserver) {

        var dto = HubEventProtoMapper.toDto(request);
        var avro = HubEventMapper.toAvro(dto);
        byte[] bytes = avroSerializer.toBytes(avro);

        kafkaPublisher.sendAndWait(
                "telemetry.hubs.v1",
                request.getHubId(),
                bytes
        );

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
