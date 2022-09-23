package ru.yandex.practicum.filmorate.exeption;


public class FilmNotFoundException extends RuntimeException  {
    public FilmNotFoundException(final String message) {
        super(message);
    }
}
