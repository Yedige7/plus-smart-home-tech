package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.dto.AddressEmbeddable;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {

    @Id
    @UuidGenerator
    private UUID deliveryId;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "from_country")),
            @AttributeOverride(name = "city", column = @Column(name = "from_city")),
            @AttributeOverride(name = "street", column = @Column(name = "from_street")),
            @AttributeOverride(name = "house", column = @Column(name = "from_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "from_flat"))
    })
    private AddressEmbeddable fromAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "to_country")),
            @AttributeOverride(name = "city", column = @Column(name = "to_city")),
            @AttributeOverride(name = "street", column = @Column(name = "to_street")),
            @AttributeOverride(name = "house", column = @Column(name = "to_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "to_flat"))
    })
    private AddressEmbeddable toAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryState deliveryState;
}
