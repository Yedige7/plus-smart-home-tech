package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory;
    private final ScenarioEngine engine;

    @Value("${app.kafka.topics.snapshots}")
    private String topic;

    private static final AtomicBoolean running = new AtomicBoolean(true);

    public void start() {
        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer =
                     (KafkaConsumer<String, SensorsSnapshotAvro>) snapshotConsumerFactory.createConsumer()) {
            consumer.subscribe(List.of(topic));
            while (running.get()) {
                var records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    continue;
                }

                for (var r : records) {
                    try {
                        SensorsSnapshotAvro snapshot = r.value();
                        engine.handleSnapshot(snapshot);
                    } catch (Exception e) {
                        log.error("Failed to handle snapshot, offset={}", r.offset(), e);
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("SnapshotProcessor crashed", e);
        }
    }

    public void stop() {
        running.set(false);
    }
}