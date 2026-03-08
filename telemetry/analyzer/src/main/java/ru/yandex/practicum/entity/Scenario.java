package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "scenarios",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_scenarios_hub_name",
                columnNames = {"hub_id", "name"}
        )
)
@Getter
@Setter
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioConditionLink> conditionLinks = new HashSet<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioActionLink> actionLinks = new HashSet<>();
}