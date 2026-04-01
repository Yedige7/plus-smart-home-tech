package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.WarehouseProductDto;
import ru.yandex.practicum.entity.WarehouseProductEntity;

@Component
public class WarehouseMapper {

    public WarehouseProductDto toDto(WarehouseProductEntity entity) {
        return new WarehouseProductDto(
                entity.getProductId(),
                entity.isFragile(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getDepth(),
                entity.getWeight(),
                entity.getQuantity()
        );
    }

    public WarehouseProductEntity toEntity(NewProductInWarehouseRequest request) {
        WarehouseProductEntity entity = new WarehouseProductEntity();
        entity.setProductId(request.getProductId());
        entity.setFragile(request.isFragile());
        entity.setWidth(request.getDimension().getWidth());
        entity.setHeight(request.getDimension().getHeight());
        entity.setDepth(request.getDimension().getDepth());
        entity.setWeight(request.getWeight());
        entity.setQuantity(0L);
        return entity;
    }
}
