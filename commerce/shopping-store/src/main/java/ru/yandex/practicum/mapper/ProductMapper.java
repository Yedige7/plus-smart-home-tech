package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.entity.ProductEntity;
import ru.yandex.practicum.enums.ProductState;

@Component
public class ProductMapper {

    public ProductDto toDto(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductDto(
                entity.getId(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getImageSrc(),
                entity.getCategory(),
                entity.getState(),
                entity.getQuantityState(),
                entity.getPrice()
        );
    }

    public ProductEntity toEntity(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setImageSrc(dto.getImageSrc());
        entity.setCategory(dto.getProductCategory());
        entity.setQuantityState(dto.getQuantityState());
        entity.setPrice(dto.getPrice());
        entity.setState(ProductState.ACTIVE);

        return entity;
    }



    public void update(ProductEntity entity, ProductDto dto) {
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setImageSrc(dto.getImageSrc());
        entity.setCategory(dto.getProductCategory());
        entity.setQuantityState(dto.getQuantityState());
        entity.setPrice(dto.getPrice());

        if (dto.getProductState() != null) {
            entity.setState(dto.getProductState());
        }
    }

}