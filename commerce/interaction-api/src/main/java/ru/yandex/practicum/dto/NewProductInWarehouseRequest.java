package ru.yandex.practicum.dto;

import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    private UUID productId;
    private boolean fragile;
    private DimensionDto dimension;
    private double weight;
}
