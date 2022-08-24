package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int id;
    private final Map<Long, User> users = new HashMap<>();

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
        checkUserId(user.getId());
        User oldUser = users.get(user.getId());
        Set<Long> oldUserFriendsId = oldUser.getFriendsId();
        user.setFriendsId(oldUserFriendsId);
        users.put(user.getId(), user);
        log.info("PUT запрос от пользователя успешно обработан");
        return user;
    }

    @Override
    public User getUser(long id) {
        checkUserId(id);
        log.info("GET запрос на получение  пользователя успешно обработан");
        return users.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        checkUserId(id);
        if (!users.get(id).getFriendsId().isEmpty()) {
            log.info("GET запрос на получение списка друзей пользователя успешно обработан.");
            return users.get(id).getFriendsId().stream()
                    .map(users::get)
                    .collect(Collectors.toList());
        } else {
            log.info("GET запрос на получение списка друзей пользователя успешно обработан. Список пуст");
            return Collections.emptyList();
        }
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

    private void checkUserId(long id) {
        if (!users.containsKey(id)) {
            log.info("Пользователь указал несуществующий id ");
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
    }

    private int idGenerator() {
        return ++id;
    }
}
