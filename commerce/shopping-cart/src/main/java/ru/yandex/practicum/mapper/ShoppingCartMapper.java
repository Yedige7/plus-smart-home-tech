package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCartEntity;
import ru.yandex.practicum.entity.ShoppingCartItemEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCartEntity cart, List<ShoppingCartItemEntity> items) {
        Map<UUID, Long> products = new LinkedHashMap<>();
        for (ShoppingCartItemEntity item : items) {
            products.put(item.getProductId(), item.getQuantity());
        }

        return new ShoppingCartDto(cart.getId(), products);
    }
}