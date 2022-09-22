package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exeption.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreByID(int id) {
        String sql = "SELECT * FROM GENRES WHERE id=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class),
                id).stream().findFirst().orElseThrow(() -> new GenreNotFoundException("Жанр не найден"));
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class)));
    }
}
