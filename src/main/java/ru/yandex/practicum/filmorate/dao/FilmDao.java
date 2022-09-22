package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    List<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(long id);

    List<Film> getPopularFilms(int count);

    void addLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);
}
