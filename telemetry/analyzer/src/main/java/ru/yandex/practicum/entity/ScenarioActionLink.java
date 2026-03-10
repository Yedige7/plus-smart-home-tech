package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
public class ScenarioActionLink {

    @EmbeddedId
    private ScenarioActionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actionId")
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}