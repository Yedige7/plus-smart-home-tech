package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderClient {
    @PostMapping("/api/v1/order/payment")
    OrderDto paymentSuccess(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);
}

