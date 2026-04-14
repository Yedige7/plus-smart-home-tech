package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private static final BigDecimal BASE_COST = new BigDecimal("5.0");
    private static final BigDecimal ADDRESS_1_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal ADDRESS_2_MULTIPLIER = new BigDecimal("2.0");
    private static final BigDecimal FRAGILE_RATE = new BigDecimal("0.2");
    private static final BigDecimal WEIGHT_RATE = new BigDecimal("0.3");
    private static final BigDecimal VOLUME_RATE = new BigDecimal("0.2");
    private static final BigDecimal STREET_DIFF_RATE = new BigDecimal("0.2");

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = deliveryMapper.toEntity(dto);

        if (delivery.getDeliveryState() == null) {
            delivery.setDeliveryState(DeliveryState.CREATED);
        }

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(saved);
    }

    public BigDecimal deliveryCost(OrderDto order) {
        if (order == null || order.getOrderId() == null) {
            throw new NoDeliveryFoundException("Не указан заказ для расчёта доставки");
        }

        Delivery delivery = deliveryRepository.findByOrderId(order.getOrderId()).orElseThrow(() -> new NoDeliveryFoundException("Доставка для заказа не найдена: " + order.getOrderId()));

        BigDecimal result = BASE_COST;

        String fromStreet = null;
        if (delivery.getFromAddress() != null) {
            fromStreet = delivery.getFromAddress().getStreet();
        }

        if (fromStreet != null) {
            if (fromStreet.contains("ADDRESS_1")) {
                result = result.add(BASE_COST.multiply(ADDRESS_1_MULTIPLIER));
            } else if (fromStreet.contains("ADDRESS_2")) {
                result = result.add(BASE_COST.multiply(ADDRESS_2_MULTIPLIER));
            }
        }

        if (Boolean.TRUE.equals(order.getFragile())) {
            result = result.add(result.multiply(FRAGILE_RATE));
        }

        if (order.getDeliveryWeight() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryWeight()).multiply(WEIGHT_RATE));
        }

        if (order.getDeliveryVolume() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryVolume()).multiply(VOLUME_RATE));
        }

        String toStreet = null;
        if (delivery.getToAddress() != null) {
            toStreet = delivery.getToAddress().getStreet();
        }

        if (fromStreet != null && toStreet != null && !fromStreet.equalsIgnoreCase(toStreet)) {
            result = result.add(result.multiply(STREET_DIFF_RATE));
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена: " + deliveryId));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(delivery.getOrderId());
        request.setDeliveryId(delivery.getDeliveryId());

        warehouseClient.shippedToDelivery(request);

        orderClient.assembly(delivery.getOrderId());
    }

    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена: " + deliveryId));

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(delivery.getOrderId());
    }

    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена: " + deliveryId));

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(delivery.getOrderId());
    }

    @Transactional
    public void deliveryPickedByOrderId(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException("Доставка для заказа не найдена: " + orderId));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(delivery.getOrderId());
        request.setDeliveryId(delivery.getDeliveryId());

        warehouseClient.shippedToDelivery(request);
        orderClient.assembly(delivery.getOrderId());
    }

    @Transactional
    public void deliverySuccessfulByOrderId(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException("Доставка для заказа не найдена: " + orderId));

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(delivery.getOrderId());
    }

    @Transactional
    public void deliveryFailedByOrderId(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException("Доставка для заказа не найдена: " + orderId));

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(delivery.getOrderId());
    }
}
