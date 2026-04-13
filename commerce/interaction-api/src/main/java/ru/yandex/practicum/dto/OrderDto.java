package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID orderId;
    private UUID shoppingCartId;
    private Map<UUID, Long> products;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
}
