package ru.yandex.practicum.exception;

public class ShoppingCartDeactivatedException extends RuntimeException {
    public ShoppingCartDeactivatedException(String message) {
        super(message);
    }
}
