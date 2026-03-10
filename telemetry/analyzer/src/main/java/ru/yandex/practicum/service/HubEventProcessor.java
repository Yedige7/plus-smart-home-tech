package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final ConsumerFactory<String, HubEventAvro> hubEventConsumerFactory;
    private final ScenarioStorageService storage;

    @Value("${app.kafka.topics.hubs}")
    private String topic;

    private static final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run() {
        try (KafkaConsumer<String, HubEventAvro> consumer =
                     (KafkaConsumer<String, HubEventAvro>) hubEventConsumerFactory.createConsumer()) {

            consumer.subscribe(List.of(topic));
            while (running.get()) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (var r : records) {
                    try {
                        storage.handleHubEvent(r.value());
                    } catch (Exception e) {
                        log.error("Failed to handle hub event, offset={}", r.offset(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("HubEventProcessor crashed", e);
        }
    }

    public void stop() {
        running.set(false);
    }
}