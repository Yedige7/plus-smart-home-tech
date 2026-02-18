package ru.practicum.common;

public class KafkaPublishException extends RuntimeException {
    private final String topic;
    private final String key;

    public KafkaPublishException(String topic, String key, Throwable cause) {
        super("Kafka publish failed. topic=" + topic + ", key=" + key, cause);
        this.topic = topic;
        this.key = key;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }
}
