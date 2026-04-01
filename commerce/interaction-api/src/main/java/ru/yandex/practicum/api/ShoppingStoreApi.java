package ru.yandex.practicum.api;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreApi {

    @PutMapping("/api/v1/shopping-store")
    ProductDto create(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto update(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    boolean remove(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getById(@PathVariable UUID productId);

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam List<String> sort
    );
}