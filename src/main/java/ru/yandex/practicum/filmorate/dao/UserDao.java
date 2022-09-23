package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    User getUser(long id);

    List<User> getFriends(long id);

    List<Long> addFriend(long idUser, long idUserFriend);

    void deleteFriend(long idUser, long idUserFriend);

    List<User> getGeneralFriends(long idUser, long idOtherUser);
}
