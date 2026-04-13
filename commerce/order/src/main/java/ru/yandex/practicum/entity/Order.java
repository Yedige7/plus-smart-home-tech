package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.dto.AddressEmbeddable;
import ru.yandex.practicum.enums.OrderState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    private UUID orderId;

    private String username;
    private UUID shoppingCartId;
    private UUID paymentId;
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;

    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "house", column = @Column(name = "delivery_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "delivery_flat"))
    })
    private AddressEmbeddable deliveryAddress;

    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();
}
