package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private static final LocalDate BOUNDARY_DATE = LocalDate.of(1895, 12, 25);
    private int id;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен GET запрос от пользователя на получение списка всех фильмов");
        if (!films.isEmpty()) {
            return new ArrayList<>(films.values());
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Пользователь передал POST запрос на публикацию фильма");
        validatorFilm(film);
        film.setId(idGenerator());
        films.put(film.getId(), film);
        log.info("POST запрос от пользователя успешно обработан");
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Пользователь передал PUT запрос на обновление фильма");
        validatorFilm(film);
        if (!films.containsKey(film.getId())) {
            log.info("Нет фильма с таким id");
            throw new ValidationException("несуществующий id при обновлении");
        }
        films.put(film.getId(), film);
        log.info("PUT запрос от пользователя успешно обработан");
        return film;
    }

    private void validatorFilm(Film film) {
        if (film.getReleaseDate().isBefore(BOUNDARY_DATE)) {
            log.warn("Пользователь передал некорректную дату фильма");
            throw new ValidationException("Ошибка в дате релиза фильма");
        }
    }

    private int idGenerator() {
        return ++id;
    }

}
