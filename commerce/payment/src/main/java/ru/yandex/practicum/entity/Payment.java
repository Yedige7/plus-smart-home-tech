package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    private UUID paymentId;

    private UUID orderId;
    private BigDecimal productTotal;
    private BigDecimal deliveryTotal;
    private BigDecimal feeTotal;
    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    private PaymentState state; // PENDING, SUCCESS, FAILED
}