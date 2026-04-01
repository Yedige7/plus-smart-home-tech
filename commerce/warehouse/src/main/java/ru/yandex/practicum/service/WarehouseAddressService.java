package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class WarehouseAddressService {

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private final String currentAddress;

    public WarehouseAddressService() {
        this.currentAddress = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];
    }

    public AddressDto getAddress() {
        return new AddressDto(
                currentAddress,
                currentAddress,
                currentAddress,
                currentAddress,
                currentAddress
        );
    }
}
