package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final UserDao userDao;
    private static final LocalDate BOUNDARY_DATE = LocalDate.of(1895, 12, 25);

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.userDao = userDao;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT*FROM FILMS";
        return new ArrayList<>(jdbcTemplate.query(sql, new FilmMapper(mpaDao, jdbcTemplate)));
    }

    @Override
    public Film createFilm(Film film) {
        validatorFilm(film);

        String sql = "INSERT INTO FILMS(NAME,DESCRIPTION,DURATION,RELEASE_DATE,MPA_ID) VALUES (?,?,?,?,?)";
        String sql2 = "SELECT ID FROM FILMS ORDER BY ID DESC LIMIT 1";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId());

        long filmId = jdbcTemplate.queryForObject(sql2, long.class);
        film.setId(filmId);
        updateGenres(film);
        return getFilm(filmId);

    }

    @Override
    public Film updateFilm(Film film) {
        validatorFilm(film);
        getFilm(film.getId());
        String sql = "UPDATE FILMS SET NAME=?,DESCRIPTION=?,DURATION=?,RELEASE_DATE=?,MPA_ID=? WHERE ID=? ";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId(), film.getId());
        String sql2 = " DELETE FROM FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(sql2, film.getId());
        updateGenres(film);
        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT*FROM FILMS WHERE ID=?";
        return jdbcTemplate.query(sql, new FilmMapper(mpaDao, jdbcTemplate), id).stream().findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count<=0){
            throw new ValidationException("Ошибка валидации параметра");
        }
        String sql = "SELECT ID,NAME,DESCRIPTION,DURATION,RELEASE_DATE,MPA_ID,count FROM (SELECT FILM_ID,COUNT(USER_ID) AS" +
                " count FROM USER_LIKED_FILM AS ULF GROUP BY FILM_ID) AS LIKES RIGHT JOIN FILMS AS F ON F.ID=LIKES.FILM_ID" +
                " ORDER BY count desc LIMIT ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new FilmMapper(mpaDao, jdbcTemplate), count));
    }

    @Override
    public void addLike(long filmId, long userId) {
        getFilm(filmId);
        userDao.getUser(userId);
        String sql = "INSERT INTO USER_LIKED_FILM(USER_ID,FILM_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        userDao.getUser(userId);
        getFilm(filmId);
        String sql = "DELETE FROM USER_LIKED_FILM WHERE USER_ID=? AND FILM_ID=?";
        jdbcTemplate.update(sql, userId, filmId);
        return getFilm(filmId);
    }

    private void validatorFilm(Film film) {
        if (film.getReleaseDate().isBefore(BOUNDARY_DATE)) {
            log.warn("Пользователь передал некорректную дату фильма");
            throw new ValidationException("Ошибка в дате релиза фильма");
        }
    }

    private void updateGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            String sql3 = "INSERT INTO FILM_GENRE(FILM_ID,GENRE_ID) VALUES (?,?)";
            new HashSet<>(genres).forEach(genre -> jdbcTemplate.update(sql3, film.getId(), genre.getId()));
        }
    }
}
