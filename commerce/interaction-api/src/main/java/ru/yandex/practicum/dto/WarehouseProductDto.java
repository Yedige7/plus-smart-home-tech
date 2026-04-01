package ru.yandex.practicum.dto;

import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProductDto {

    private UUID productId;
    private boolean fragile;
    private double width;
    private double height;
    private double depth;
    private double weight;
    private long quantity;

}