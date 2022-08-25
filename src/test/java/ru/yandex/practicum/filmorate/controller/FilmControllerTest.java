package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController controller;
    Film film = new Film("Стена", "о стене", LocalDate.of(1895, 12, 28),
            2);
    Film film2 = new Film("Стена", "о стене", LocalDate.of(1702, 1, 1),
            3);
    Film film3 = new Film("Корова", "о корове", LocalDate.of(1895, 12, 28),
            2);

    Film film4 = new Film("А", "о А", LocalDate.of(2002, 1, 1),
            2);
    Film film5 = new Film("Б", "о Б", LocalDate.of(2002, 1, 1),
            2);
    Film film6 = new Film("В", "о В", LocalDate.of(2002, 1, 1),
            2);

    @BeforeEach
    void createController() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage()));
    }

    @Test
    void findAllWithNoFilms() {
        assertEquals(0, controller.findAll().size(), "Список не пустой");
    }

    @Test
    void findAllWithFilm() {
        controller.createFilm(film);
        assertEquals("Стена", controller.findAll().get(0).getName(), "Фильм не добавлен");
    }

    @Test
    void createFilmWithCorrectDate() {
        controller.createFilm(film);
        assertEquals("Стена", controller.findAll().get(0).getName(), "Фильм не добавлен");
    }

    @Test
    void createFilmWithIncorrectDate() {
        assertThrows(ValidationException.class, () -> controller.createFilm(film2));
    }

    @Test
    void updateFilmWithCorrectId() {
        controller.createFilm(film);
        film3.setId(1);
        controller.updateFilm(film3);
        assertEquals("Корова", controller.findAll().get(0).getName(), "Фильм не обновлен");
    }

    @Test
    void updateFilmWithNoId() {
        controller.createFilm(film);
        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film3));
    }

    @Test
    void updateFilmWithIncorrectId() {
        controller.createFilm(film);
        film3.setId(-2);
        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film3));
    }

    @Test
    void createFilmWithoutLikes() {
        controller.createFilm(film);

        assertEquals(0, controller.getFilm(film.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей не пуст");
        assertEquals(0, controller.getFilm(film.getId()).getLikes(), "Количество лайков должно быть 0");
    }

    @Test
    void addLikeNewFilm() {
        controller.createFilm(film);
        controller.addLike(film.getId(), 1);

        assertEquals(1, controller.getFilm(film.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей пуст");
        assertEquals(1, controller.getFilm(film.getId()).getLikes(),
                "Количество лайков не должно быть 0");
    }

    @Test
    void addLikeWithIncorrectFilmId() {
        controller.createFilm(film);
        assertThrows(FilmNotFoundException.class, () -> controller.addLike(88, 1));
    }

    @Test
    void deleteLikeFilmWithCorrectId() {
        controller.createFilm(film);
        controller.addLike(film.getId(), 1);

        assertEquals(1, controller.getFilm(film.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей пуст");
        assertEquals(1, controller.getFilm(film.getId()).getLikes(),
                "Количество лайков не должно быть 0");

        controller.deleteLike(film.getId(), 1);
        assertEquals(0, controller.getFilm(film.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей не пуст");
        assertEquals(0, controller.getFilm(film.getId()).getLikes(), "Количество лайков должно быть 0");
    }

    @Test
    void deleteLikeFilmWithIncorrectId() {
        controller.createFilm(film);
        controller.addLike(film.getId(), 1);

        assertEquals(1, controller.getFilm(film.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей пуст");
        assertEquals(1, controller.getFilm(film.getId()).getLikes(),
                "Количество лайков не должно быть 0");

        assertThrows(FilmNotFoundException.class, () -> controller.deleteLike(88, 1));
    }

    @Test
    void getPopularFilmsWithIncorrectCount() {
        assertThrows(ValidationException.class, () -> controller.getPopularFilms(0));
    }

    @Test
    void getPopularFilmsWithEmptyListFilms() {
        assertEquals(0, controller.getPopularFilms(1).size());
    }

    @Test
    void getPopularFilms() {
        controller.createFilm(film4);
        controller.createFilm(film5);
        controller.createFilm(film6);

        assertEquals(3, controller.getPopularFilms(10).size());

        //ставим лайки и смотрим результат
        controller.addLike(film4.getId(), 1);
        controller.addLike(film4.getId(), 2);
        controller.addLike(film4.getId(), 3);
        controller.addLike(film4.getId(), 4);
        controller.addLike(film5.getId(), 1);
        controller.addLike(film5.getId(), 2);

        assertEquals(film4, controller.getPopularFilms(10).get(0), "Неверная статистика");
        assertEquals(film6, controller.getPopularFilms(10).get(2), "Неверная статистика");
    }

    @Test
    void getFilmWithCorrectId() {
        controller.createFilm(film);
        String filmName = controller.getFilm(film.getId()).getName();
        assertEquals("Стена", filmName, "Не получен фильм");
    }

    @Test
    void getFilmWithInCorrectId() {
        assertThrows(FilmNotFoundException.class, () -> controller.getFilm(88));
    }
}