package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
    // все тесты пишем только для кастомной валидации
    @Test
    void FilmControllerTest() throws ValidationException {

        // POST запросы

        // добавляем фильм c верной датой (граничное условие) и смотрим результат
        FilmController controller = new FilmController(new FilmService(new InMemoryFilmStorage()));
        Film film = new Film("Стена", "о стене", LocalDate.of(1895, 12, 28),
                2);
        controller.createFilm(film);
        assertEquals("Стена", controller.findAll().get(0).getName(), "Фильм не добавлен");

        // добавляем фильм c неверной датой и смотрим результат
        Film film2 = new Film("Стена", "о стене", LocalDate.of(1702, 1, 1),
                3);
        assertThrows(ValidationException.class, () -> controller.createFilm(film2));

        // PUT запросы

        // указываем верный id при обновлении и смотрим результат
        Film film3 = new Film("Корова", "о корове", LocalDate.of(1895, 12, 28),
                2);
        film3.setId(1);
        controller.updateFilm(film3);
        assertEquals("Корова", controller.findAll().get(0).getName(), "Фильм не обновлен");

        // не указываем id при обновлении и смотрим результат
        Film film4 = new Film("Стена", "о стене", LocalDate.of(1992, 1, 1),
                3);
        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film4));

        // указываем несуществующий id при обновлении
        Film film5 = new Film("Стена", "о стене", LocalDate.of(1902, 1, 1),
                2);
        film5.setId(88);
        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film5));
        //______________________________________________________________________________________
        // тестируем новый функционал

        // PUT запрос на лайк

        // создаем фильм и проверяем лайки
        Film film6 = new Film("Дверь", "о двери", LocalDate.of(2002, 1, 1),
                2);
        controller.createFilm(film6);

        assertEquals(0, controller.getFilm(film6.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей не пуст");
        assertEquals(0, controller.getFilm(film6.getId()).getLikes(), "Количество лайков должно быть 0");

        // добавляем лайк фильму и проверяем лайки
        controller.addLike(film6.getId(), 1);

        assertEquals(1, controller.getFilm(film6.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей пуст");
        assertEquals(1, controller.getFilm(film6.getId()).getLikes(),
                "Количество лайков не должно быть 0");

        // добавляем лайк фильму с неверным id  и смотрим результат
        assertThrows(FilmNotFoundException.class, () -> controller.addLike(88, 1));

        // DELETE запрос на удаление лайка

        // удаляем лайк фильму и смотрим результат
        controller.deleteLike(film6.getId(), 1);

        assertEquals(0, controller.getFilm(film6.getId()).getLikedUsersId().size(),
                "Список лайкнувших пользователей не пуст");
        assertEquals(0, controller.getFilm(film6.getId()).getLikes(), "Количество лайков должно быть 0");

        // удаляем лайк фильму с неверным id и смотрим результат
        assertThrows(FilmNotFoundException.class, () -> controller.deleteLike(88, 1));

        // GET запрос на получение фильма

        // с верным id
        String filmName = controller.getFilm(film6.getId()).getName();
        assertEquals("Дверь", filmName, "Не получен фильм");

        // с несуществующим id
        assertThrows(FilmNotFoundException.class, () -> controller.getFilm(88));

        //GET запрос на получение популярных фильмов

        FilmController controller2 = new FilmController(new FilmService(new InMemoryFilmStorage()));

        // проверяем при пустом списке фильмов
        assertEquals(0, controller2.getPopularFilms(1).size());

        // проверяем при неверном параметре count
        assertThrows(ValidationException.class, () -> controller2.getPopularFilms(0));

        // создаем три фильма без лайков и пробуем получить список
        Film film7 = new Film("А", "о А", LocalDate.of(2002, 1, 1),
                2);
        Film film8 = new Film("Б", "о Б", LocalDate.of(2002, 1, 1),
                2);
        Film film9 = new Film("В", "о В", LocalDate.of(2002, 1, 1),
                2);
        controller2.createFilm(film7);
        controller2.createFilm(film8);
        controller2.createFilm(film9);

        assertEquals(3, controller2.getPopularFilms(10).size());

        //ставим лайки и смотрим результат
        controller2.addLike(film7.getId(), 1);
        controller2.addLike(film7.getId(), 2);
        controller2.addLike(film7.getId(), 3);
        controller2.addLike(film7.getId(), 4);
        controller2.addLike(film8.getId(), 1);
        controller2.addLike(film8.getId(), 2);

        assertEquals(film7, controller2.getPopularFilms(10).get(0), "Неверная статистика");
        assertEquals(film9, controller2.getPopularFilms(10).get(2), "Неверная статистика");
    }

    @Test
    void USERControllerTest() {
        //POST запросы

        // добавляем пользователя с пробелом в логине и смотрим результат
        UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
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
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(user5));

        // указываем несуществующий id при обновлении
        User user6 = new User("s@mail.ru", "kapa", LocalDate.of(1895, 12, 28));
        user6.setId(88);
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(user6));
        //______________________________________________________________________________________
        // тестируем новый функционал

        UserController controller2 = new UserController(new UserService(new InMemoryUserStorage()));

        // PUT запрос на добавление в друзья

        // создаем 2-х пользователей и проверяем список друзей
        User user8 = new User("a@mail.ru", "papa", LocalDate.of(1897, 12, 28));
        User user7 = new User("b@mail.ru", "mama", LocalDate.of(1895, 12, 28));

        controller2.createUser(user8);
        controller2.createUser(user7);

        assertEquals(0, controller2.getUserFriends(user8.getId()).size(), "Список друзей не пуст");
        assertEquals(0, controller2.getUserFriends(user7.getId()).size(), "Список друзей не пуст");

        // добавляем друга c неверным id
        assertThrows(UserNotFoundException.class, () -> controller2.addFriend(-2, 88));
        // добавляем друга проверяем список друзей
        controller2.addFriend(user8.getId(), user7.getId());

        assertEquals(1, controller2.getUserFriends(user8.getId()).size(), "Список друзей пуст");
        assertEquals(1, controller2.getUserFriends(user7.getId()).size(), "Список друзей пуст");

        // DELETE запрос на удаление друга.

        // удаляем друга c неверным id
        assertThrows(UserNotFoundException.class, () -> controller2.deleteFriend(-2, 88));

        // удаляем друга c верным id
        controller2.deleteFriend(user8.getId(), user7.getId());

        assertEquals(0, controller2.getUserFriends(user8.getId()).size(), "Список друзей не пуст");
        assertEquals(0, controller2.getUserFriends(user7.getId()).size(), "Список друзей не пуст");

        // GET запрос на получение списка друзей

        // получаем список друзей с несуществующим пользователем
        assertThrows(UserNotFoundException.class, () -> controller2.getUserFriends(-2));

        // получаем список друзей с существующим пользователем без друзей
        assertEquals(0, controller2.getUserFriends(user7.getId()).size(), "Список друзей не пуст");

        // получаем список друзей с существующим пользователем с другом
        controller2.addFriend(user8.getId(), user7.getId());
        assertEquals(1, controller2.getUserFriends(user7.getId()).size(), "Список друзей пуст");

        // GET запрос на получение общего списка друзей

        // проверяем без общих друзей
        assertEquals(0, controller2.getGeneralUserFriends(user7.getId(), user8.getId()).size(),
                "Список общих друзей не пуст");

        // проверяем с одним общим  другом
        User user9 = new User("t@mail.ru", "tetya", LocalDate.of(1997, 12, 28));
        controller2.createUser(user9);

        controller2.addFriend(user9.getId(), user8.getId());
        controller2.addFriend(user9.getId(), user7.getId());

        assertEquals(1, controller2.getGeneralUserFriends(user7.getId(), user8.getId()).size(),
                "Список общих друзей  пуст");
    }
}
