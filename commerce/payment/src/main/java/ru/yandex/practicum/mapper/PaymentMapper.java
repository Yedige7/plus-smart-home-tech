package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.entity.Payment;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setTotalPayment(payment.getTotalPayment());
        dto.setDeliveryTotal(payment.getDeliveryTotal());
        dto.setFeeTotal(payment.getFeeTotal());
        return dto;
    }
}
