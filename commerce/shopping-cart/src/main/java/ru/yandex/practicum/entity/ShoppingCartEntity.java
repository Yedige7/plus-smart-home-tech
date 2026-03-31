package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.enums.ShoppingCartState;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Data
public class ShoppingCartEntity {

    @Id
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ShoppingCartState state;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

}