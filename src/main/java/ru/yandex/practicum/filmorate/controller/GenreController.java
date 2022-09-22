package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RestController
public class GenreController {
    private final GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping("/genres")
    public List<Genre> findAll() {
        log.info("Получен GET запрос от пользователя на получение всех жанров");
        return genreDao.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        log.info("Получен GET запрос от пользователя на получение жанра");
        return genreDao.getGenreByID(id);
    }
}
