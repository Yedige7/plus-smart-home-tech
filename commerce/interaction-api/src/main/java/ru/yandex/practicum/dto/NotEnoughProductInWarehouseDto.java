package ru.yandex.practicum.dto;

import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEnoughProductInWarehouseDto {
    private UUID productId;
    private long requestedQuantity;
    private long availableQuantity;
}
