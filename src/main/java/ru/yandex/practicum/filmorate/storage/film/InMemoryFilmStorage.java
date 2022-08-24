package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate BOUNDARY_DATE = LocalDate.of(1895, 12, 25);
    private long id;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        if (!films.isEmpty()) {
            return new ArrayList<>(films.values());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Film createFilm(Film film) {
        validatorFilm(film);
        film.setId(idGenerator());
        films.put(film.getId(), film);
        log.info("запрос от пользователя успешно обработан");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validatorFilm(film);
        checkFilmId(film.getId());
        Film oldFilm = films.get(film.getId());
        int oldFilmLikes = oldFilm.getLikes();
        Set<Long> oldFilmLikedUsersId = oldFilm.getLikedUsersId();
        film.setLikes(oldFilmLikes);
        film.setLikedUsersId(oldFilmLikedUsersId);
        films.put(film.getId(), film);
        log.info("запрос от пользователя успешно обработан");
        return film;
    }

    @Override
    public Film getFilm(long id) {
        checkFilmId(id);
        return films.get(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть больше 0");
        }
        if (films.isEmpty()) {
            log.info("GET запрос на популярные фильмы успешно обработан.Пользователь получил пустой список");
            return Collections.emptyList();
        } else {
            log.info("GET запрос на популярные фильмы успешно обработан.");
            return findAll().stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getLikes(), o1.getLikes()))
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private void validatorFilm(Film film) {
        if (film.getReleaseDate().isBefore(BOUNDARY_DATE)) {
            log.warn("Пользователь передал некорректную дату фильма");
            throw new ValidationException("Ошибка в дате релиза фильма");
        }
    }

    private void checkFilmId(long id) {
        if (!films.containsKey(id)) {
            log.info("Нет фильма с таким id");
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
    }

    private long idGenerator() {
        return ++id;
    }
}
