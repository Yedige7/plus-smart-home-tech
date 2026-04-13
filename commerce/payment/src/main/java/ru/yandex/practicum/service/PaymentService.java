package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;

    public BigDecimal productCost(OrderDto order) {
        validateOrderForProductCost(order);

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = shoppingStoreClient.getProduct(productId);
            if (product == null || product.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        "Не удалось получить цену товара " + productId
                );
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            total = total.add(itemTotal);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalCost(OrderDto order) {
        if (order == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Заказ отсутствует");
        }

        if (order.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указана стоимость товаров");
        }

        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указана стоимость доставки");
        }

        BigDecimal vat = order.getProductPrice()
                .multiply(VAT_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        return order.getProductPrice()
                .add(vat)
                .add(order.getDeliveryPrice())
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public PaymentDto payment(OrderDto order) {
        if (order == null || order.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Некорректный заказ");
        }

        BigDecimal productTotal = order.getProductPrice();
        if (productTotal == null) {
            productTotal = productCost(order);
        }

        BigDecimal deliveryTotal = order.getDeliveryPrice();
        if (deliveryTotal == null) {
            throw new NotEnoughInfoInOrderToCalculateException(
                    "Нельзя сформировать оплату без стоимости доставки"
            );
        }

        BigDecimal feeTotal = productTotal.multiply(VAT_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = productTotal
                .add(feeTotal)
                .add(deliveryTotal)
                .setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setProductTotal(productTotal.setScale(2, RoundingMode.HALF_UP));
        payment.setDeliveryTotal(deliveryTotal.setScale(2, RoundingMode.HALF_UP));
        payment.setFeeTotal(feeTotal);
        payment.setTotalPayment(totalPayment);
        payment.setState(PaymentState.PENDING);

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(
                        "Оплата не найдена: " + paymentId
                ));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.paymentSuccess(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(
                        "Оплата не найдена: " + paymentId
                ));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }

    private void validateOrderForProductCost(OrderDto order) {
        if (order == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Заказ отсутствует");
        }

        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }
    }
}