package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.entity.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    void deleteByHubIdAndName(String hubId, String name);

    @Query("""
                select distinct s from Scenario s
                left join fetch s.conditionLinks cl
                left join fetch cl.sensor
                left join fetch cl.condition
                left join fetch s.actionLinks al
                left join fetch al.sensor
                left join fetch al.action
                where s.hubId = :hubId
            """)
    List<Scenario> findFullByHubId(@Param("hubId") String hubId);
}
