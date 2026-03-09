package ru.yandex.practicum.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
public class HubEventDeserializer implements Deserializer<HubEventAvro> {

    @Override
    public HubEventAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            SpecificDatumReader<HubEventAvro> reader =
                    new SpecificDatumReader<>(HubEventAvro.getClassSchema());

            return reader.read(null, DecoderFactory.get().binaryDecoder(data, null));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to deserialize HubEventAvro from topic " + topic, e
            );
        }
    }
}