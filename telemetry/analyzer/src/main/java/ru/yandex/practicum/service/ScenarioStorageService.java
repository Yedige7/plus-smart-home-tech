package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.repository.*;

@Service
@RequiredArgsConstructor
public class ScenarioStorageService {

    private final ScenarioRepository scenarioRepo;
    private final SensorRepository sensorRepo;
    private final ConditionRepository conditionRepo;
    private final ActionRepository actionRepo;
    private final ScenarioConditionLinkRepository condLinkRepo;
    private final ScenarioActionLinkRepository actionLinkRepo;

    @Transactional
    public void upsertSensor(String hubId, String sensorId) {
        sensorRepo.findByIdAndHubId(sensorId, hubId).orElseGet(() -> {
            Sensor s = new Sensor();
            s.setId(sensorId);
            s.setHubId(hubId);
            return sensorRepo.save(s);
        });
    }

    @Transactional
    public void deleteSensor(String hubId, String sensorId) {
        condLinkRepo.deleteBySensor_IdAndSensor_HubId(sensorId, hubId);
        actionLinkRepo.deleteBySensor_IdAndSensor_HubId(sensorId, hubId);
        sensorRepo.deleteByIdAndHubId(sensorId, hubId);
    }

    @Transactional
    public void upsertScenario(String hubId, ScenarioAddedEventAvro dto) {
        Scenario scenario = scenarioRepo.findByHubIdAndName(hubId, dto.getName().toString())
                .orElseGet(() -> {
                    Scenario s = new Scenario();
                    s.setHubId(hubId);
                    s.setName(dto.getName().toString());
                    return s;
                });

        for (ScenarioConditionAvro c : dto.getConditions()) {
            upsertSensor(hubId, c.getSensorId().toString());
        }
        for (DeviceActionAvro a : dto.getActions()) {
            upsertSensor(hubId, a.getSensorId().toString());
        }

        scenario.getConditionLinks().clear();
        scenario.getActionLinks().clear();
        scenario = scenarioRepo.save(scenario);

        for (ScenarioConditionAvro c : dto.getConditions()) {
            Condition cond = new Condition();
            cond.setType(c.getType().name());
            cond.setOperation(c.getOperation().name());
            cond.setValue(conditionValueAsInt(c));
            cond = conditionRepo.save(cond);

            Sensor sensor = sensorRepo.findByIdAndHubId(c.getSensorId().toString(), hubId).orElseThrow();

            ScenarioConditionLink link = new ScenarioConditionLink();
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setCondition(cond);
            link.setId(new ScenarioConditionKey(scenario.getId(), sensor.getId(), cond.getId()));
            scenario.getConditionLinks().add(link);
        }

        for (DeviceActionAvro a : dto.getActions()) {
            Action action = new Action();
            action.setType(a.getType().name());
            action.setValue(a.getValue() == null ? null : (Integer) a.getValue());
            action = actionRepo.save(action);

            Sensor sensor = sensorRepo.findByIdAndHubId(a.getSensorId().toString(), hubId).orElseThrow();

            ScenarioActionLink link = new ScenarioActionLink();
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setAction(action);
            link.setId(new ScenarioActionKey(scenario.getId(), sensor.getId(), action.getId()));
            scenario.getActionLinks().add(link);
        }

        scenarioRepo.save(scenario);
    }

    @Transactional
    public void deleteScenario(String hubId, String name) {
        scenarioRepo.deleteByHubIdAndName(hubId, name);
    }

    private Integer conditionValueAsInt(ScenarioConditionAvro c) {
        Object value = c.getValue();
        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }
        if (value instanceof Integer i) {
            return i;
        }
        return 0;
    }

    public void handleHubEvent(HubEventAvro event) {
        if (event == null) return;

        String hubId = event.getHubId().toString();

        if (event.getPayload() instanceof ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro added) {
            upsertSensor(hubId, added.getId().toString());
            return;
        }

        if (event.getPayload() instanceof ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro removed) {
            deleteSensor(hubId, removed.getId().toString());
            return;
        }

        if (event.getPayload() instanceof ScenarioAddedEventAvro scenarioAdded) {
            upsertScenario(hubId, scenarioAdded);
            return;
        }

        if (event.getPayload() instanceof ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro scenarioRemoved) {
            deleteScenario(hubId, scenarioRemoved.getName().toString());
        }
    }
}