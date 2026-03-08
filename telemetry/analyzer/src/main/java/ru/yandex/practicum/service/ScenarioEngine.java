package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Condition;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEngine {

    private final ScenarioRepository scenarioRepo;
    private final HubRouterCommandSender commandSender;

    @Transactional(readOnly = true)
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        if (snapshot == null) {
            log.warn("Received null snapshot");
            return;
        }

        String hubId = snapshot.getHubId();

        List<Scenario> scenarios = scenarioRepo.findFullByHubId(hubId);

        for (Scenario scenario : scenarios) {

            boolean triggered = scenarioTriggered(snapshot, scenario);
            if (triggered) {
                scenario.getActionLinks().forEach(link -> {
                    commandSender.send(snapshot, scenario, link);
                });
            }
        }
    }

    private boolean scenarioTriggered(SensorsSnapshotAvro snapshot, Scenario scenario) {

        return scenario.getConditionLinks().stream().allMatch(link -> {

            Condition c = link.getCondition();
            String sensorId = link.getSensor().getId();

            OptionalInt actual = SnapshotValueExtractor.extract(
                    snapshot,
                    sensorId,
                    c.getType()
            );

            if (actual.isEmpty()) {
                log.warn("Condition check failed: no value in snapshot for sensorId={} type={}",
                        sensorId,
                        c.getType());
                return false;
            }

            int left = actual.getAsInt();
            int right = c.getValue();

            String op = c.getOperation().toUpperCase(Locale.ROOT);

            boolean result = switch (op) {
                case "EQUALS" -> left == right;
                case "GREATER_THAN" -> left > right;
                case "LOWER_THAN" -> left < right;
                default -> false;
            };

            return result;
        });
    }
}