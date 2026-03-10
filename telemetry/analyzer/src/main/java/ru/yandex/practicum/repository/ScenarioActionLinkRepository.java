package ru.yandex.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ScenarioActionLink;


public interface ScenarioActionLinkRepository extends JpaRepository<ScenarioActionLink, Long> {
    void deleteBySensor_IdAndSensor_HubId(String sensorId, String hubId);
}
