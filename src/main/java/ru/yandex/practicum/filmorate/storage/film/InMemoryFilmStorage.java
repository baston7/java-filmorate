package ru.yandex.practicum.filmorate.storage.film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate BOUNDARY_DATE = LocalDate.of(1895, 12, 25);
    private int id;
    private final HashMap<Integer, Film> films = new HashMap<>();

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
        log.info("POST запрос от пользователя успешно обработан");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
