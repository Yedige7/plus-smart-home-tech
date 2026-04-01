package ru.yandex.practicum.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemDto {
    private UUID productId;
    private long quantity;
}
