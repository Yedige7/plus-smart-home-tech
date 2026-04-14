package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    public List<OrderDto> getClientOrders(String username) {
        return orderRepository.findAllByUsername(username)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request, String username) {
        Order order = new Order();
        order.setUsername(username);
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(request.getShoppingCart().getProducts());
        order.setDeliveryAddress(orderMapper.toEmbeddable(request.getDeliveryAddress()));
        order.setState(OrderState.NEW);

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrderEntity(orderId);

        BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(order.getProducts(), order.getOrderId())
        );

        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());
        order.setState(OrderState.ASSEMBLED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrderEntity(orderId);

        AddressDto fromAddress = warehouseClient.getWarehouseAddress();
        AddressDto toAddress = new AddressDto(
                order.getDeliveryAddress().getCountry(),
                order.getDeliveryAddress().getCity(),
                order.getDeliveryAddress().getStreet(),
                order.getDeliveryAddress().getHouse(),
                order.getDeliveryAddress().getFlat()
        );

        DeliveryDto planned = deliveryClient.planDelivery(
                new DeliveryDto(
                        null,
                        fromAddress,
                        toAddress,
                        order.getOrderId(),
                        DeliveryState.CREATED
                )
        );

        order.setDeliveryId(planned.getDeliveryId());

        BigDecimal deliveryCost = deliveryClient.deliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrderEntity(orderId);

        BigDecimal productCost = paymentClient.productCost(orderMapper.toDto(order));
        order.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.getTotalCost(orderMapper.toDto(order));
        order.setTotalPrice(totalCost);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getDeliveryPrice() == null) {
            calculateDeliveryCost(orderId);
            order = getOrderEntity(orderId);
        }

        if (order.getProductPrice() == null || order.getTotalPrice() == null) {
            calculateTotalCost(orderId);
            order = getOrderEntity(orderId);
        }

        PaymentDto payment = paymentClient.payment(orderMapper.toDto(order));
        order.setPaymentId(payment.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = getOrderEntity(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrderEntity(request.getOrderId());

        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order getOrderEntity(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));
    }
}