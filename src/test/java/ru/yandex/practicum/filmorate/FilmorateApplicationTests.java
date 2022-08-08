package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@JacksonStdImpl
class FilmorateApplicationTests {
    // все тесты пишем только для кастомной валидации
    @Test
    void FilmControllerTest() throws ValidationException {

        //POST запросы

        // добавляем фильм c верной датой (граничное условие) и смотрим результат
        FilmController controller = new FilmController();
        Film film = new Film("Стена", "о стене", LocalDate.of(1895, 12, 28), 2);
        controller.createFilm(film);
        assertEquals("Стена", controller.findAll().get(0).getName(), "Фильм не добавлен");

        // добавляем фильм c неверной датой и смотрим результат
        Film film2 = new Film("Стена", "о стене", LocalDate.of(1702, 1, 1), 3);
        assertThrows(ValidationException.class, () -> controller.createFilm(film2));

        //PUT запросы

        // указываем верный id при обновлении и смотрим результат
        Film film3 = new Film("Корова", "о корове", LocalDate.of(1895, 12, 28), 2);
        film3.setId(1);
        controller.updateFilm(film3);
        assertEquals("Корова", controller.findAll().get(0).getName(), "Фильм не обновлен");

        // не указываем id при обновлении и смотрим результат
        Film film4 = new Film("Стена", "о стене", LocalDate.of(1992, 1, 1), 3);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film4));

        // указываем несуществующий id при обновлении
        Film film5 = new Film("Стена", "о стене", LocalDate.of(1902, 1, 1), 2);
        film5.setId(88);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film5));
    }


    @Test
    void USERControllerTest() {
        //POST запросы

        // добавляем пользователя с пробелом в логине и смотрим результат
        UserController controller = new UserController();
        User user = new User("s@mail.ru", "p apa", LocalDate.of(1895, 12, 28));
        assertThrows(ValidationException.class, () -> controller.createUser(user));

        // добавляем пользователя без пробела в логине и без имени и смотрим результат
        User user2 = new User("s@mail.ru", "papa", LocalDate.of(1895, 12, 28));
        user2.setName(null);
        controller.createUser(user2);

        assertEquals(1, controller.findAll().size(), "Пользователь не создан");
        assertEquals("papa", controller.findAll().get(0).getName(), "Имя не совпадает с логином");

        // добавляем пользователя с логином и именем и смотрим результат
        User user3 = new User("s@mail.ru", "papa", LocalDate.of(1895, 12, 28));
        user3.setName("daddy");
        controller.createUser(user3);
        assertEquals("daddy", controller.findAll().get(1).getName(), "Имя не совпадает с назначенным");


        //PUT запросы

        // указываем верный id при обновлении и смотрим результат
        User user4 = new User("s@mail.ru", "lapa", LocalDate.of(1895, 12, 28));
        user4.setId(1);
        controller.updateUser(user4);
        assertEquals("lapa", controller.findAll().get(0).getLogin(), "Пользователь не обновлен");

        // не указываем id при обновлении и смотрим результат
        User user5 = new User("s@mail.ru", "kapa", LocalDate.of(1895, 12, 28));
        assertThrows(ValidationException.class, () -> controller.updateUser(user5));

        // указываем несуществующий id при обновлении
        User user6 = new User("s@mail.ru", "kapa", LocalDate.of(1895, 12, 28));
        user6.setId(88);
        assertThrows(ValidationException.class, () -> controller.updateUser(user6));

    }

}
