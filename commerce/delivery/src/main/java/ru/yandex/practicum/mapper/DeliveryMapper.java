package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AddressEmbeddable;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.entity.Delivery;


@Component
public class DeliveryMapper {

    public DeliveryDto toDto(Delivery delivery) {
        DeliveryDto dto = new DeliveryDto();
        dto.setDeliveryId(delivery.getDeliveryId());
        dto.setOrderId(delivery.getOrderId());
        dto.setFromAddress(toDto(delivery.getFromAddress()));
        dto.setToAddress(toDto(delivery.getToAddress()));
        dto.setDeliveryState(delivery.getDeliveryState());
        return dto;
    }

    public Delivery toEntity(DeliveryDto dto) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(dto.getDeliveryId());
        delivery.setOrderId(dto.getOrderId());
        delivery.setFromAddress(toEmbeddable(dto.getFromAddress()));
        delivery.setToAddress(toEmbeddable(dto.getToAddress()));
        delivery.setDeliveryState(dto.getDeliveryState());
        return delivery;
    }

    public AddressDto toDto(AddressEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        AddressDto dto = new AddressDto();
        dto.setCountry(embeddable.getCountry());
        dto.setCity(embeddable.getCity());
        dto.setStreet(embeddable.getStreet());
        dto.setHouse(embeddable.getHouse());
        dto.setFlat(embeddable.getFlat());
        return dto;
    }

    public AddressEmbeddable toEmbeddable(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        AddressEmbeddable embeddable = new AddressEmbeddable();
        embeddable.setCountry(dto.getCountry());
        embeddable.setCity(dto.getCity());
        embeddable.setStreet(dto.getStreet());
        embeddable.setHouse(dto.getHouse());
        embeddable.setFlat(dto.getFlat());
        return embeddable;
    }
}
