package ru.practicum.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.avro.AvroBinarySerializer;
import ru.practicum.dto.hubs.HubEventDto;
import ru.practicum.dto.sensors.SensorEventDto;
import ru.practicum.kafka.KafkaPublisher;
import ru.practicum.mapper.HubEventMapper;
import ru.practicum.mapper.SensorEventMapper;

@RestController
@RequestMapping("/events")
public class EventsController {

    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    private final KafkaPublisher publisher;
    private final String sensorsTopic;
    private final String hubsTopic;

    public EventsController(
            KafkaPublisher publisher,
            @Value("${app.kafka.topics.sensors}") String sensorsTopic,
            @Value("${app.kafka.topics.hubs}") String hubsTopic
    ) {
        this.publisher = publisher;
        this.sensorsTopic = sensorsTopic;
        this.hubsTopic = hubsTopic;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEventDto event) {

        var avro = SensorEventMapper.toAvro(event);
        byte[] bytes = AvroBinarySerializer.toBytes(avro);

        publisher.sendAndWait(sensorsTopic, event.getHubId(), bytes);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEventDto event) {
        var avro = HubEventMapper.toAvro(event);
        byte[] bytes = AvroBinarySerializer.toBytes(avro);

        publisher.sendAndWait(hubsTopic, event.getHubId(), bytes);

        return ResponseEntity.ok().build();
    }
}
