package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    private final FilmController controller;
    private final UserController userController;
    Genre genre = new Genre(1);
    Genre genre2 = new Genre(2);
    Genre genre3 = new Genre(3);
    Film film = new Film("Стена", "о стене", LocalDate.of(1995, 12, 20),
            2, new Mpa(1), List.of(genre, genre2)
    );
    Film film2 = new Film("Стена", "о стене", LocalDate.of(1702, 1, 1),
            3, new Mpa(1), List.of(genre, genre2));
    Film film3 = new Film("Корова", "о корове", LocalDate.of(1895, 12, 28),
            2, new Mpa(1), List.of(genre3));

    Film film4 = new Film("А", "о А", LocalDate.of(2002, 1, 1),
            2, new Mpa(1), List.of(genre, genre2));
    Film film5 = new Film("Б", "о Б", LocalDate.of(2002, 1, 1),
            2, new Mpa(1), List.of(genre, genre2));
    Film film6 = new Film("В", "о В", LocalDate.of(2002, 1, 1),
            2, new Mpa(1), List.of(genre, genre2));
    Film film7 = new Film("В", "о В", LocalDate.of(2002, 1, 1),
            2, null, List.of(genre, genre2));
    Film film8 = new Film("В", "о В", LocalDate.of(2002, 1, 1),
            2, new Mpa(1), Collections.emptyList());

    User user4 = new User("b@mail.ru", "mama", "mama", LocalDate.of(1895, 12, 28));
    User user5 = new User("a@mail.ru", "papa", "daddy", LocalDate.of(1897, 12, 28));
    User user6 = new User("t@mail.ru", "tetya", "tetka", LocalDate.of(1997, 12, 28));
    User user7 = new User("l@mail.ru", "letya", "letka", LocalDate.of(1997, 12, 28));

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
    void createFilmWithWithoutMpa() {
        assertThrows(Throwable.class, () -> controller.createFilm(film7));
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
        film3.setId(88);
        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film3));
    }

    @Test
    void updateFilmWithCorrectGenre() {
        controller.createFilm(film);
        film3.setId(1);
        controller.updateFilm(film3);
        assertEquals(1, controller.findAll().get(0).getGenres().size(), "Жанр не обновлен");
    }

    @Test
    void updateFilmWithOutGenre() {
        controller.createFilm(film);
        film8.setId(1);
        controller.updateFilm(film8);
        assertEquals(0, controller.findAll().get(0).getGenres().size(), "Жанр не пуст");
    }

    @Test
    void addLikeNewFilm() {
        userController.createUser(user4);
        controller.createFilm(film);
        controller.createFilm(film4);
        controller.createFilm(film5);
        controller.createFilm(film6);
        controller.addLike(3, 1);

        assertEquals("Б", controller.getPopularFilms(5).get(0).getName(),
                "Лайк не добавлен");
    }

    @Test
    void addLikeWithIncorrectFilmId() {
        userController.createUser(user4);
        controller.createFilm(film);
        assertThrows(FilmNotFoundException.class, () -> controller.addLike(88, 1));
    }

    @Test
    void deleteLikeFilmWithCorrectId() {
        userController.createUser(user4);
        userController.createUser(user5);
        userController.createUser(user6);

        controller.createFilm(film);
        controller.createFilm(film4);
        controller.createFilm(film5);
        controller.createFilm(film6);

        controller.addLike(1, 1);
        controller.addLike(2, 1);
        controller.addLike(3, 1);
        controller.addLike(2, 2);
        controller.addLike(3, 2);
        controller.addLike(3, 3);
        assertEquals("Б", controller.getPopularFilms(5).get(0).getName(),
                "Лайк не добавлен");
        controller.deleteLike(3, 3);
        controller.deleteLike(3, 2);
        assertEquals("А", controller.getPopularFilms(5).get(0).getName(),
                "Лайк не удален");
    }

    @Test
    void deleteLikeFilmWithIncorrectId() {
        userController.createUser(user4);

        controller.createFilm(film);
        controller.createFilm(film4);
        controller.createFilm(film5);
        controller.createFilm(film6);

        controller.addLike(3, 1);

        assertEquals("Б", controller.getPopularFilms(5).get(0).getName(),
                "Лайк не добавлен");

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
        userController.createUser(user4);
        userController.createUser(user5);
        userController.createUser(user6);
        userController.createUser(user7);

        controller.createFilm(film4);
        controller.createFilm(film5);
        controller.createFilm(film6);

        assertEquals(3, controller.getPopularFilms(10).size());

        //ставим лайки и смотрим результат
        controller.addLike(1, 1);
        controller.addLike(1, 2);
        controller.addLike(1, 3);
        controller.addLike(1, 4);
        controller.addLike(2, 1);
        controller.addLike(2, 2);

        assertEquals("А", controller.getPopularFilms(10).get(0).getName(), "Неверная статистика");
        assertEquals("В", controller.getPopularFilms(10).get(2).getName(), "Неверная статистика");
    }

    @Test
    void getFilmWithCorrectId() {
        controller.createFilm(film);
        String filmName = controller.getFilm(1).getName();
        assertEquals("Стена", filmName, "Не получен фильм");
    }

    @Test
    void getFilmWithInCorrectId() {
        assertThrows(FilmNotFoundException.class, () -> controller.getFilm(88));
    }
}