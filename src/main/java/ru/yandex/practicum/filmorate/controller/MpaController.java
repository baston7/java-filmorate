package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@RestController
public class MpaController {
    private final MpaDao mpaDao;

    @Autowired
    public MpaController(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @GetMapping("/mpa")
    public List<Mpa> findAll() {
        log.info("Получен GET запрос от пользователя на получение всех рейтингов");
        return mpaDao.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable int id) {
        log.info("Получен GET запрос от пользователя на получение рейтинга");
        return mpaDao.getMpaByID(id);
    }
}
