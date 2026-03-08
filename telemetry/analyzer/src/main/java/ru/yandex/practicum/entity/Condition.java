package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "conditions")
@Getter
@Setter
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "value", nullable = false)
    private Integer value;
}