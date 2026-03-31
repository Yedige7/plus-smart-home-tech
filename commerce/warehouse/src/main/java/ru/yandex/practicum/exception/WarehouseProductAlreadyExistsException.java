package ru.yandex.practicum.exception;

public class WarehouseProductAlreadyExistsException extends RuntimeException {
    public WarehouseProductAlreadyExistsException(String message) {
        super(message);
    }
}
