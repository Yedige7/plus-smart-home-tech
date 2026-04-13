package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/cost")
    public BigDecimal deliveryCost(@RequestBody OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }

    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID orderId) {
        deliveryService.deliveryPickedByOrderId(orderId);
    }

    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID orderId) {
        deliveryService.deliverySuccessfulByOrderId(orderId);
    }

    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID orderId) {
        deliveryService.deliveryFailedByOrderId(orderId);
    }
}
