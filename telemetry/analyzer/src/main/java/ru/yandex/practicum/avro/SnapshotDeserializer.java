package ru.yandex.practicum.avro;


import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {
    public SnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}