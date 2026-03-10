package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ScenarioConditionKey;
import ru.yandex.practicum.entity.ScenarioConditionLink;


public interface ScenarioConditionLinkRepository extends JpaRepository<ScenarioConditionLink, ScenarioConditionKey> {
    void deleteBySensor_IdAndSensor_HubId(String sensorId, String hubId);
}