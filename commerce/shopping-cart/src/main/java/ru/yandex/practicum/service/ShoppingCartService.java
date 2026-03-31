package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProducts(String username, Map<UUID, Long> products);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto removeProducts(String username, List<UUID> productIds);

    void deactivate(String username);
}