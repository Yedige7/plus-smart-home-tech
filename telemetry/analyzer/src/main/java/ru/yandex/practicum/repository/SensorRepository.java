package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Sensor;

import java.util.Collection;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    void deleteByIdAndHubId(String id, String hubId);
}
