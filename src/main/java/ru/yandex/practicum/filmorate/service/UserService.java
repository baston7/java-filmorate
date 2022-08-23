package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Long> addFriend(long idUser, long idUserFriend) {
        User user = userStorage.getUser(idUser);
        User userFriend = userStorage.getUser(idUserFriend);
        if (!user.getFriendsId().contains(idUserFriend)) {
            user.getFriendsId().add(idUserFriend);
            userFriend.getFriendsId().add(idUser);
            log.info("PUT запрос на получение общих друзей успешно обработан.");
        } else {
            throw new UserNotFoundException("Пользователь уже существует в друзьях");
        }
        return new ArrayList<>(user.getFriendsId());
    }

    public List<Long> deleteFriend(long idUser, long idUserFriend) {
        User user = userStorage.getUser(idUser);
        User userFriend = userStorage.getUser(idUserFriend);
        user.getFriendsId().remove(idUserFriend);
        userFriend.getFriendsId().remove(idUser);
        log.info("DELETE запрос на удаление друга успешно обработан.");
        return new ArrayList<>(user.getFriendsId());
    }

    public List<User> getUserFriends(long idUser) {
        return userStorage.getFriends(idUser);
    }

    public List<User> getGeneralFriends(long idUser, long idOtherUser) {
        User user = userStorage.getUser(idUser);
        User otherUser = userStorage.getUser(idOtherUser);

        List<User> friendsUser = new ArrayList<>(userStorage.getFriends(idUser));
        List<User> friendsOtherUser = new ArrayList<>(userStorage.getFriends(idOtherUser));

        Set<Long> idsUserFriends = user.getFriendsId();
        Set<Long> idsOtherUserFriends = otherUser.getFriendsId();

        if (idsUserFriends.equals(idsOtherUserFriends)) {
            log.info("GET запрос на получение общих друзей успешно обработан. У пользователей все друзья общие");
            return userStorage.getFriends(idUser);
        } else {
            log.info("GET запрос на получение общих друзей успешно обработан.");
            friendsUser.retainAll(friendsOtherUser);
            return friendsUser;
        }
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }
}
