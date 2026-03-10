package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sensors")
@Getter
@Setter
public class Sensor {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;
}
