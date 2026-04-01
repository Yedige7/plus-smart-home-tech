package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PageResponseDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    public ShoppingStoreController(ShoppingStoreService shoppingStoreService) {
        this.shoppingStoreService = shoppingStoreService;
    }

    @PutMapping
    public ProductDto create(@RequestBody ProductDto productDto) {
        return shoppingStoreService.create(productDto);
    }

    @PostMapping
    public ProductDto update(@RequestBody ProductDto productDto) {
        return shoppingStoreService.update(productDto);
    }

    @GetMapping("/{productId}")
    public ProductDto getById(@PathVariable UUID productId) {
        return shoppingStoreService.getById(productId);
    }

    @GetMapping
    public PageResponseDto<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort
    ) {
        return shoppingStoreService.getProducts(category, page, size, sort);
    }

    @PostMapping("/removeProductFromStore")
    public boolean remove(@RequestBody UUID productId) {
        return shoppingStoreService.remove(productId);
    }

    @PostMapping("/quantityState")
    public boolean setQuantityState(@RequestParam UUID productId,
                                    @RequestParam QuantityState quantityState) {
        return shoppingStoreService.setProductQuantityState(productId, quantityState);
    }
}