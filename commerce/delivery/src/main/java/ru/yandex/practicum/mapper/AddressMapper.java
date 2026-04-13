package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AddressEmbeddable;

@Component
public class AddressMapper {

    public AddressDto toDto(AddressEmbeddable address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getHouse(),
                address.getFlat()
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