package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int id;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        if (!users.isEmpty()) {
            return new ArrayList<>(users.values());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public User createUser(User user) {
        validatorUser(user);
        checkAndSetUserName(user);
        user.setId(idGenerator());
        users.put(user.getId(), user);
        log.info("POST запрос от пользователя успешно обработан");
        return user;
    }

    @Override
    public User updateUser(User user) {
        validatorUser(user);
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь указал несуществующий id при обновлении");
            throw new ValidationException("несуществующий id при обновлении");
        }
        checkAndSetUserName(user);
        users.put(user.getId(), user);
        log.info("PUT запрос от пользователя успешно обработан");
        return user;
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

    private int idGenerator() {
        return ++id;
    }
}
