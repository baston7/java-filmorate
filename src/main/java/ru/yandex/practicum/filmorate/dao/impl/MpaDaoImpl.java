package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exeption.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaByID(int id) {
        String sql = "SELECT*FROM MPA WHERE ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Mpa.class), id).stream().findFirst()
                .orElseThrow(() -> new MpaNotFoundException("Рейтинг не найден"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT*FROM MPA";
        return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Mpa.class)));
    }
}
