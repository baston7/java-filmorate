package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    private final UserController controller;
    User user = new User("s@mail.ru", "p apa", "daddy", LocalDate.of(1895, 12, 28));
    User user2 = new User("s@mail.ru", "papa", "daddy", LocalDate.of(1895, 12, 28));
    User user3 = new User("s@mail.ru", "lapa", "lapka", LocalDate.of(1895, 12, 28));
    User user4 = new User("b@mail.ru", "mama", "mama", LocalDate.of(1895, 12, 28));
    User user5 = new User("a@mail.ru", "papa", "daddy", LocalDate.of(1897, 12, 28));
    User user6 = new User("t@mail.ru", "tetya", "tetka", LocalDate.of(1997, 12, 28));

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
        user2.setName("lol");
        controller.createUser(user2);
        assertEquals("lol", controller.findAll().get(0).getName(), "Имя не совпадает с назначенным");
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
        User user = controller.createUser(user2);
        user.setId(88);
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(user));
    }

    @Test
    void createUsersAndCheckFriends() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);

        assertEquals(0, controller.getUserFriends(user1.getId()).size(), "Список друзей не пуст");
        assertEquals(0, controller.getUserFriends(user2.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void addFriendWithIncorrectId() {
        controller.createUser(user5);
        controller.createUser(user4);
        assertThrows(UserNotFoundException.class, () -> controller.addFriend(-2, 88));
    }

    @Test
    void addCorrectFriend() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);
        controller.addFriend(user1.getId(), user2.getId());

        assertEquals(1, controller.getUserFriends(user1.getId()).size(), "Список друзей пуст");
        assertEquals(0, controller.getUserFriends(user2.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void deleteFriendWithIncorrectId() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);
        controller.addFriend(user1.getId(), user2.getId());
        assertThrows(UserNotFoundException.class, () -> controller.deleteFriend(-2, 88));
    }

    @Test
    void deleteFriendWithCorrectId() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user2.getId(), user1.getId());

        controller.deleteFriend(user1.getId(), user2.getId());

        assertEquals(0, controller.getUserFriends(user1.getId()).size(), "Список друзей не пуст");
        assertEquals(1, controller.getUserFriends(user2.getId()).size(), "Список друзей пуст");
    }

    @Test
    void getUserFriendsWithoutUsers() {
        assertThrows(UserNotFoundException.class, () -> controller.getUserFriends(-2));
    }

    @Test
    void getUserFriendsWithUserWithoutFriends() {
        User user = controller.createUser(user4);
        assertEquals(0, controller.getUserFriends(user.getId()).size(), "Список друзей не пуст");
    }

    @Test
    void getGeneralUserFriendsWithoutGeneral() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);
        controller.addFriend(user1.getId(), user2.getId());
        assertEquals(0, controller.getGeneralUserFriends(user1.getId(), user2.getId()).size(),
                "Список общих друзей не пуст");
    }

    @Test
    void getGeneralUserFriendsWithGeneralFriends() {
        User user1 = controller.createUser(user5);
        User user2 = controller.createUser(user4);
        User user3 = controller.createUser(user6);

        controller.addFriend(user1.getId(), user3.getId());
        controller.addFriend(user2.getId(), user3.getId());
        assertEquals(1, controller.getGeneralUserFriends(user2.getId(), user1.getId()).size(),
                "Список общих друзей  пуст");
    }
}
