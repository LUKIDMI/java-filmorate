package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей. Количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("У пользователя с ID={} не указано имя", user.getId());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);

        log.info("Добавлен новый пользователь: {} (ID={})", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @NotNull @Valid User newUser) {
        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка добавить пользователя с несуществующим ID={}", newUser.getId());
            throw new IllegalArgumentException("Пользователь с ID=" + newUser.getId() + " не найден.");
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (newUser.getName().isBlank()) {
            oldUser.setName(oldUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        log.info("Информация о пользователе {} (ID={}) обновлена.", oldUser.getName(), oldUser.getId());
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
