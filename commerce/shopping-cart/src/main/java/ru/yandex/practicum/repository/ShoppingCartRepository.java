package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ShoppingCartEntity;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {
    Optional<ShoppingCartEntity> findByUsername(String username);
}
