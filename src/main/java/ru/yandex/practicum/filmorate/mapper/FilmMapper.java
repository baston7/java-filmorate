package ru.yandex.practicum.filmorate.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmMapper implements RowMapper<Film> {
    private final MpaDao mpaDao;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmMapper(MpaDao mpaDao, JdbcTemplate jdbcTemplate) {
        this.mpaDao = mpaDao;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("ID"));
        film.setName(rs.getString("NAME"));
        film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setDuration(rs.getInt("DURATION"));
        int mpa_id = rs.getInt("MPA_ID");
        Mpa mpa = mpaDao.getMpaByID(mpa_id);
        film.setMpa(mpa);
        String sql = "SELECT G.ID,G.NAME FROM GENRES AS G JOIN FILM_GENRE AS FG ON G.ID=FG.GENRE_ID WHERE FG.FILM_ID=?";
        List<Genre> genreList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), film.getId());
        film.setGenres(genreList);
        return film;
    }
}
