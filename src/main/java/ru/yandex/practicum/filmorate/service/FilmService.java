package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikedUsersId().contains(userId)) {
            int likes = film.getLikes();
            film.setLikes(likes + 1);
            film.getLikedUsersId().add(userId);
            log.info("PUT запрос на лайк успешно обработан");
        }
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        int newLikes = film.getLikes() - 1;
        if (!film.getLikedUsersId().contains(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            film.setLikes(newLikes - 1);
            film.getLikedUsersId().remove(userId);
            log.info("DELETE запрос успешно обработан.Лайк убран");
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }


}
