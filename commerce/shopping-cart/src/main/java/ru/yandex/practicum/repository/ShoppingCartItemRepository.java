package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ShoppingCartItemEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItemEntity, UUID> {

    List<ShoppingCartItemEntity> findAllByCartId(UUID cartId);

    Optional<ShoppingCartItemEntity> findByCartIdAndProductId(UUID cartId, UUID productId);
}