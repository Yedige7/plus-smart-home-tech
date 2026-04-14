package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;

@FeignClient(name = "delivery")
public interface DeliveryClient {

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@RequestBody DeliveryDto request);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto order);
}