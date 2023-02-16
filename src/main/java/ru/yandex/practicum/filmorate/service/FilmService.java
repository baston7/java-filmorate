package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
@Service
public class FilmService {
    private final FilmDao filmDao;


    @Autowired
    public FilmService(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    public List<Film> findAll() {
        return filmDao.findAll();
    }


    public Film createFilm(Film film) {
       return filmDao.createFilm(film);
    }


    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film);
    }


    public Film getFilm(long id) {
        return filmDao.getFilm(id);
    }


    public List<Film> getPopularFilms(int count) {
        return filmDao.getPopularFilms(count);
    }


    public void addLike(long filmId, long userId) {
        filmDao.addLike(filmId,userId);
    }


    public Film deleteLike(long filmId, long userId) {

        return filmDao.deleteLike(filmId,userId);
    }

}