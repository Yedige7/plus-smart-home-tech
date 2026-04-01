package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.WarehouseProductEntity;

import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProductEntity, UUID> {
}
