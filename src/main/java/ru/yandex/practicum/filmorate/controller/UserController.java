package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен GET запрос от пользователя на получение списка всех пользователей");
        return userService.findAll();
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен POST запрос от пользователя на создание пользователя");
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен PUT запрос от пользователя на обновление пользователя");
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public List<Long> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен PUT запрос от пользователя на добавление в друзья");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public List<Long> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен DELETE запрос от пользователя на удаление из друзей");
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        log.info("Получен GET запрос от пользователя на получение списка друзей");
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Получен GET запрос от пользователя на получение пользователя");
        return userService.getUser(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getGeneralUserFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен GET запрос от пользователя на получение списка общих друзей");
        return userService.getGeneralFriends(id, otherId);
    }
}


