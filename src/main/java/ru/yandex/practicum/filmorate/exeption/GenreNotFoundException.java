package ru.yandex.practicum.filmorate.exeption;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(final String message) {
        super(message);
    }
}