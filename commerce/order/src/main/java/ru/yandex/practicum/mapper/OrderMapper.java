package ru.yandex.practicum.mapper;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AddressEmbeddable;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.entity.Order;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderDto(
                order.getOrderId(),
                order.getShoppingCartId(),
                order.getProducts(),
                order.getPaymentId(),
                order.getDeliveryId(),
                order.getState(),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice(),
                order.getDeliveryPrice(),
                order.getProductPrice()
        );
    }

    public AddressEmbeddable toEmbeddable(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return new AddressEmbeddable(
                dto.getCountry(),
                dto.getCity(),
                dto.getStreet(),
                dto.getHouse(),
                dto.getFlat()
        );
    }
}
