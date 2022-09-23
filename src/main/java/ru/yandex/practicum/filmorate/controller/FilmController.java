package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен GET запрос от пользователя на получение списка всех фильмов");
        return filmService.findAll();
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Пользователь передал POST запрос на публикацию фильма");
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Пользователь передал PUT запрос на обновление фильма");
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Передан PUT запрос на лайк");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Передан DELETE запрос на удаление лайка");
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Получен GET запрос на просмотр популярных фильмов");
        return filmService.getPopularFilms(count);
    }

    @GetMapping("films/{id}")
    public Film getFilm(@PathVariable long id) {
        log.info("Получен GET запрос на получение фильма");
        return filmService.getFilm(id);
    }

}
