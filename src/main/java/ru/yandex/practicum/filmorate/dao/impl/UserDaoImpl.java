package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class)));
    }

    @Override
    public User createUser(User user) {
        validatorUser(user);
        checkAndSetUserName(user);
        String sql = "INSERT INTO USERS(NAME,LOGIN,EMAIL,BIRTHDAY) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());
        String sql2 = "SELECT ID FROM USERS ORDER BY ID DESC LIMIT 1";
        long userId = jdbcTemplate.queryForObject(sql2, long.class);
        return getUser(userId);
    }

    @Override
    public User updateUser(User user) {
        validatorUser(user);
        checkAndSetUserName(user);
        String sql = "UPDATE USERS SET NAME=?,LOGIN=?,EMAIL=?,BIRTHDAY=? WHERE ID=? ";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        return getUser(user.getId());
    }

    @Override
    public User getUser(long id) {
        String sql = "SELECT ID,NAME,LOGIN,EMAIL,BIRTHDAY FROM USERS WHERE ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id).stream().findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getFriends(long id) {
        getUser(id);
        String sql = "SELECT ID,NAME,LOGIN,EMAIL,BIRTHDAY FROM USERS AS U LEFT JOIN FRIENDS AS F ON U.ID=F.FRIEND_ID" +
                " WHERE F.USER_ID=?";
            return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id));
    }

    @Override
    public List<Long> addFriend(long idUser, long idUserFriend) {
        getUser(idUser);
        getUser(idUserFriend);
        String sql = "INSERT INTO FRIENDS(USER_ID,FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, idUser, idUserFriend);
        String sql3 = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID=?";
        return jdbcTemplate.query(sql3, new BeanPropertyRowMapper<>(User.class), idUser).stream().map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(long idUser, long idUserFriend) {
        getUser(idUser);
        getUser(idUserFriend);
        String sql = "DELETE FROM FRIENDS WHERE USER_ID=? AND FRIEND_ID=? ";
        jdbcTemplate.update(sql, idUser, idUserFriend);
    }

    @Override
    public List<User> getGeneralFriends(long idUser, long idOtherUser) {
        getUser(idUser);
        getUser(idOtherUser);
        String sql = "SELECT * FROM USERS WHERE ID IN(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID=? AND FRIEND_ID" +
                " IN(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID=?))";
        return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), idUser, idOtherUser));
    }

    private void checkAndSetUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пользователь не указал имя пользователя в запросе");
            user.setName(user.getLogin());
        }
    }

    private void validatorUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Пользователь при создании логина использовал пробельные символы");
            throw new ValidationException("Логин содержит пробельные символы");
        }
    }
}
