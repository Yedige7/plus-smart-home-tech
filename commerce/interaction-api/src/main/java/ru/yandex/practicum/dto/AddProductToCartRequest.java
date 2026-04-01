package ru.yandex.practicum.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToCartRequest {
    private String username;
    private UUID productId;
    private long quantity;
}
