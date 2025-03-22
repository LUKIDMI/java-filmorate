package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

/**
 * Контроллер для управления пользователями.
 * <p>
 * Предоставляет REST API для операций с пользователями, таких как создание, обновление, удаление,
 * а также управление списком друзей.
 */
@RestController
@Validated
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Возвращает список всех пользователей в хранилище.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на список всех пользователей, количество: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    /**
     * Возвращает пользователя по указанному идентификатору.
     *
     * @param userId идентификатор пользователя, должен быть положительным
     * @return ResponseEntity с найденным пользователем и статусом 200 (OK)
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) {
        log.info("Получен запрос на пользователя с ID={}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /**
     * Добавляет нового пользователя в хранилище.
     *
     * @param user объект пользователя, содержащий данные для добавления, не должен быть null
     * @return ResponseEntity с добавленным пользователем и статусом 201 (Created)
     * @throws jakarta.validation.ConstraintViolationException если данные пользователя не прошли валидацию
     * @throws IllegalArgumentException                        если пользователь с таким ID уже существует
     */
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        log.info("Получен запрос на добавление пользователя с email={}", user.getEmail());
        User addedUser = userService.addUser(user);
        log.info("Пользователь {} с ID={} успешно добавлен", addedUser.getName(), addedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(addedUser);
    }

    /**
     * Обновляет существующего пользователя в хранилище на основе предоставленных данных.
     * <p>
     * Метод принимает объект пользователя с обновлёнными данными и заменяет существующего пользователя
     * с таким же ID. Если пользователь с указанным ID не найден, будет выброшено исключение.
     *
     * @param updUser объект пользователя с обновлёнными данными, не должен быть null, ID обязателен
     * @return ResponseEntity с обновлённым пользователем и статусом 200 (OK)
     * @throws jakarta.validation.ConstraintViolationException если данные пользователя не прошли валидацию
     * @throws UserNotFoundException                           если пользователь с указанным ID не найден
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody @NotNull @Valid User updUser) {
        log.info("Получен запрос на обновление пользователя с ID={}", updUser.getId());
        return ResponseEntity.ok(userService.updateUser(updUser));
    }

    /**
     * Удаляет пользователя из хранилища по указанному идентификатору.
     * <p>
     * Метод удаляет пользователя с указанным ID из хранилища. Если пользователь не найден,
     * будет выброшено исключение, и клиент получит статус 404 (Not Found).
     *
     * @param userId идентификатор пользователя, который необходимо удалить, должен быть положительным
     * @return ResponseEntity с пустым телом и статусом 204 (No Content)
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        log.info("Получен запрос на удаление пользователя с ID={}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Добавляет пользователя в список друзей другого пользователя.
     * <p>
     * Метод создаёт дружескую связь между двумя пользователями. Связь является двунаправленной:
     * оба пользователя добавляются в списки друзей друг друга. Если один из пользователей не найден,
     * будет выброшено исключение.
     *
     * @param userId   идентификатор пользователя, который добавляет друга, должен быть положительным
     * @param friendId идентификатор пользователя, которого добавляют в друзья, должен быть положительным
     * @return ResponseEntity с обновлённым пользователем (инициатором дружбы) и статусом 200 (OK)
     * @throws UserNotFoundException    если один из пользователей с указанным ID не найден
     * @throws IllegalArgumentException если пользователь пытается добавить в друзья самого себя
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        log.info("Получен запрос на добавление в друзья пользователей с ID={} и ID={}", userId, friendId);
        userService.addFriend(userId, friendId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /**
     * Удаляет дружескую связь между двумя пользователями.
     * <p>
     * Метод удаляет двунаправленную дружескую связь: оба пользователя удаляются из списков друзей
     * друг друга. Если один из пользователей не найден, будет выброшено исключение.
     *
     * @param userId   идентификатор пользователя, который удаляет друга, должен быть положительным
     * @param friendId идентификатор пользователя, которого удаляют из друзей, должен быть положительным
     * @return ResponseEntity с пустым телом и статусом 204 (No Content)
     * @throws UserNotFoundException если один из пользователей с указанным ID не найден
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос на удаление дружеской связи между пользователями с ID={} и ID={}", userId, friendId);
        userService.deleteFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Возвращает список друзей указанного пользователя.
     *
     * @param userId идентификатор пользователя, чьих друзей нужно найти, должен быть положительным
     * @return ResponseEntity с набором друзей пользователя и статусом 200 (OK)
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getAllUserFriends(@PathVariable("id") Long userId) {
        log.info("Получен запрос на поиск всех друзей пользователя с ID={}", userId);
        return ResponseEntity.ok(userService.getAllUserFriends(userId));
    }

    /**
     * Возвращает список общих друзей двух пользователей.
     *
     * @param userId  идентификатор первого пользователя, должен быть положительным
     * @param otherId идентификатор второго пользователя, должен быть положительным
     * @return ResponseEntity с набором общих друзей и статусом 200 (OK)
     * @throws UserNotFoundException если один из пользователей с указанным ID не найден
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherId) {
        log.info("Получен запрос на поиск общих друзей пользователей с ID={} и ID={}", userId, otherId);
        return ResponseEntity.ok(userService.getCommonFriends(userId, otherId));
    }
}