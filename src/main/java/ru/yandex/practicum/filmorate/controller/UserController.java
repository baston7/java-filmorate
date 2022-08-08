package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private int id;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен GET запрос от пользователя на получение списка всех пользователей");
        if (!users.isEmpty()) {
            return new ArrayList<>(users.values());
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен POST запрос от пользователя на создание пользователя");
        validatorUser(user);
        if ((user.getName()==null||user.getName().isBlank())){
            log.info("Пользователь не указал имя при регистрации");
            user.setName(user.getLogin());
        }
        user.setId(idGenerator());
        users.put(user.getId(), user);
        log.info("POST запрос от пользователя успешно обработан");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен PUT запрос от пользователя на обновление пользователя");
        validatorUser(user);
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь указал несуществующий id при обновлении");
            throw new ValidationException("несуществующий id при обновлении");
        }
        if (user.getName()==null||user.getName().isBlank()) {
            log.info("Пользователь не указал имя при обновлении");
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("PUT запрос от пользователя успешно обработан");
        return user;
    }

    private void validatorUser(User user) throws ValidationException {
        if (containsWhiteSpace(user.getLogin())) {
            log.warn("Пользователь при создании логина использовал пробельные символы");
            throw new ValidationException("Логин содержит пробельные символы");
        }
    }
    //метод для проверки наличия пробела в тексте
    public static boolean containsWhiteSpace(final String string) {
        if (string != null) {
            for (int i = 0; i < string.length(); i++) {
                if (Character.isWhitespace(string.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }



        private int idGenerator() {
            return ++id;
        }

}
