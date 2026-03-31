package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageResponseDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;

import java.util.List;
import java.util.UUID;


public interface ShoppingStoreService {

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto getById(UUID productId);

    PageResponseDto<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort);

    boolean remove(UUID productId);

    boolean setProductQuantityState(UUID productId, QuantityState quantityState);
}