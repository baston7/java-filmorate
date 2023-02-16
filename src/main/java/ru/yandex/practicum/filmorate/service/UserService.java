package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }


    public User createUser(User user) {
        return userDao.createUser(user);
    }


    public User updateUser(User user) {
        return userDao.updateUser(user);
    }


    public User getUser(long id) {
        return userDao.getUser(id);
    }


    public List<User> getFriends(long id) {
        return userDao.getFriends(id);
    }


    public List<Long> addFriend(long idUser, long idUserFriend) {
        return userDao.addFriend(idUser, idUserFriend);
    }


    public void deleteFriend(long idUser, long idUserFriend) {
        userDao.deleteFriend(idUser, idUserFriend);
    }


    public List<User> getGeneralFriends(long idUser, long idOtherUser) {
        return userDao.getGeneralFriends(idUser, idOtherUser);
    }
}
