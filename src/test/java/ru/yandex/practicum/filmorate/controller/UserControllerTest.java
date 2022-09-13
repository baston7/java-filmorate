package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController controller;
    User user = new User("s@mail.ru", "p apa", LocalDate.of(1895, 12, 28));
    User user2 = new User("s@mail.ru", "papa", LocalDate.of(1895, 12, 28));
    User user3 = new User("s@mail.ru", "lapa", LocalDate.of(1895, 12, 28));
    User user4 = new User("b@mail.ru", "mama", LocalDate.of(1895, 12, 28));
    User user5 = new User("a@mail.ru", "papa", LocalDate.of(1897, 12, 28));
    User user6 = new User("t@mail.ru", "tetya", LocalDate.of(1997, 12, 28));

    @BeforeEach
    void createController() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
        ;
    }

    @Test
    void findAllWithNoUsers() {
        assertEquals(0, controller.findAll().size(), "Список не пустой");
    }

    @Test
    void findAllWithUsers() {
        controller.createUser(user4);
        assertEquals("mama", controller.findAll().get(0).getLogin(), "Пользователь не добавлен");
    }


    @Test
    void createUserWithSpaceInLogin() {
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void createUserWithCorrectLoginAndNoName() {
        user2.setName(null);
        controller.createUser(user2);

        assertEquals(1, controller.findAll().size(), "Пользователь не создан");
        assertEquals("papa", controller.findAll().get(0).getName(), "Имя не совпадает с логином");
    }

    @Test
    void createUserWithCorrectLoginAndName() {
        user2.setName("daddy");
        controller.createUser(user2);
        assertEquals("daddy", controller.findAll().get(0).getName(), "Имя не совпадает с назначенным");
    }


    @Test
    void updateUserWithCorrectId() {
        controller.createUser(user2);
        user3.setId(1);
        controller.updateUser(user3);
        assertEquals("lapa", controller.findAll().get(0).getLogin(), "Пользователь не обновлен");
    }

    @Test
    void updateUserWithNoId() {
        controller.createUser(user2);
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(user3));
    }

    @Test
    void updateUserWithIncorrectId() {
        controller.createUser(user2);
        user3.setId(88);
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(user3));
    }

    @Test
    void createUsersAndCheckFriends() {
        controller.createUser(user5);
        controller.createUser(user4);

        assertEquals(0, controller.getUserFriends(user5.getId()).size(), "Список друзей не пуст");
        assertEquals(0, controller.getUserFriends(user4.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void addFriendWithIncorrectId() {
        controller.createUser(user5);
        controller.createUser(user4);
        assertThrows(UserNotFoundException.class, () -> controller.addFriend(-2, 88));
    }

    @Test
    void addCorrectFriend() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.addFriend(user5.getId(), user4.getId());

        assertEquals(1, controller.getUserFriends(user5.getId()).size(), "Список друзей пуст");
        assertEquals(1, controller.getUserFriends(user4.getId()).size(), "Список друзей пуст");
    }

    @Test
    void deleteFriendWithIncorrectId() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.addFriend(user5.getId(), user4.getId());
        assertThrows(UserNotFoundException.class, () -> controller.deleteFriend(-2, 88));
    }

    @Test
    void deleteFriendWithCorrectId() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.addFriend(user5.getId(), user4.getId());
        controller.deleteFriend(user5.getId(), user4.getId());

        assertEquals(0, controller.getUserFriends(user5.getId()).size(), "Список друзей не пуст");
        assertEquals(0, controller.getUserFriends(user4.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void getUserFriendsWithoutUsers() {
        assertThrows(UserNotFoundException.class, () -> controller.getUserFriends(-2));
    }

    @Test
    void getUserFriendsWithUserWithoutFriends() {
        controller.createUser(user4);
        assertEquals(0, controller.getUserFriends(user4.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void getUserFriendsWithUserAndFriend() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.addFriend(user5.getId(), user4.getId());
        assertEquals(1, controller.getUserFriends(user4.getId()).size(), "Список друзей пуст");
    }


    @Test
    void getGeneralUserFriendsWithoutGeneral() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.addFriend(user5.getId(), user4.getId());
        assertEquals(0, controller.getGeneralUserFriends(user4.getId(), user5.getId()).size(),
                "Список общих друзей не пуст");
    }

    @Test
    void getGeneralUserFriendsWithGeneralFriends() {
        controller.createUser(user5);
        controller.createUser(user4);
        controller.createUser(user6);

        controller.addFriend(user6.getId(), user5.getId());
        controller.addFriend(user6.getId(), user4.getId());
        assertEquals(1, controller.getGeneralUserFriends(user4.getId(), user5.getId()).size(),
                "Список общих друзей  пуст");
    }
}
