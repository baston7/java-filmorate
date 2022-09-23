package ru.yandex.practicum.filmorate.exeption;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(final String message) {
        super(message);
    }
}