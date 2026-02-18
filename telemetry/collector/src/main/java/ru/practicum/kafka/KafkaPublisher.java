package ru.practicum.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.common.KafkaPublishException;

import java.time.Duration;

@Service
public class KafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAndWait(String topic, String key, byte[] value) {
        try {
            kafkaTemplate.send(topic, key, value).get(Duration.ofSeconds(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Kafka send failed: topic={}, key={}, idOrName={}",
                    topic, key, e);
            throw new KafkaPublishException(topic, key, e);
        }
    }
}
