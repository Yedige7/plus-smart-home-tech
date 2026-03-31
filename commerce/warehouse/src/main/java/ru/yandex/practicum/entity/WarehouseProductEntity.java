package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "warehouse_products")
public class WarehouseProductEntity {

    @Id
    private UUID productId;

    @Column(name = "fragile", nullable = false)
    private boolean fragile;

    @Column(name = "width", nullable = false)
    private double width;

    @Column(name = "height", nullable = false)
    private double height;

    @Column(name = "depth", nullable = false)
    private double depth;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
