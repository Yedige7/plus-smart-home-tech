package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.avro.AvroBinarySerializer;
import ru.practicum.aggregator.avro.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotAggregator aggregator = new SnapshotAggregator();
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${app.kafka.topics.sensors}")
    private String sensorsTopic;
    @Value("${app.kafka.topics.snapshots}")
    private String snapshotsTopic;
    private KafkaConsumer<String, SensorEventAvro> consumer;
    private KafkaProducer<String, byte[]> producer;

    public void start() {
        consumer = buildConsumer();
        producer = buildProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook: wakeup consumer");
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(Collections.singletonList(sensorsTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    Optional<SensorsSnapshotAvro> updated = aggregator.updateState(event);

                    if (updated.isPresent()) {
                        SensorsSnapshotAvro snapshot = updated.get();

                        byte[] payload = AvroBinarySerializer.toBytes(snapshot);

                        ProducerRecord<String, byte[]> out =
                                new ProducerRecord<>(snapshotsTopic, snapshot.getHubId().toString(), payload);

                        producer.send(out, (metadata, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send snapshot for hubId={}", snapshot.getHubId(), ex);
                            }
                        });
                    }
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                try {
                    producer.flush();
                } finally {
                    try {
                        consumer.commitSync();
                    } catch (Exception e) {
                        log.warn("commitSync failed on shutdown", e);
                    }
                }
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }

    private KafkaConsumer<String, SensorEventAvro> buildConsumer() {
        Properties p = new Properties();

        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "telemetry-aggregator");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        p.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());

        return new KafkaConsumer<>(p);
    }

    private KafkaProducer<String, byte[]> buildProducer() {
        Properties p = new Properties();

        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        p.put(ProducerConfig.LINGER_MS_CONFIG, "5");

        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        return new KafkaProducer<>(p);
    }
}