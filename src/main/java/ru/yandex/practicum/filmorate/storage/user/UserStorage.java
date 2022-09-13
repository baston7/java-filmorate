package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    User getUser(long id);

    List<User> getFriends(long id);
}
