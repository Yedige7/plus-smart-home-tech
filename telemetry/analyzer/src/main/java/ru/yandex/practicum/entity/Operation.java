package ru.yandex.practicum.entity;

import java.util.Locale;

public enum Operation {
    GREATER, LESS, EQUAL;

    public static Operation fromDb(String s) {
        return Operation.valueOf(s.toUpperCase(Locale.ROOT));
    }

    public boolean test(int left, int right) {
        return switch (this) {
            case GREATER -> left > right;
            case LESS -> left < right;
            case EQUAL -> left == right;
        };
    }
}
