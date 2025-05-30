package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * <p>
 * Предоставляет методы для работы с пользователями, включая добавление, обновление, удаление,
 * получение списка пользователей, а также управление списком друзей. Использует {@link UserStorage}
 * для хранения данных.
 */
@Service
@Slf4j
public class UserService {

    /**
     * Хранилище пользователей, используемое для операций с данными.
     */
    private final UserStorage userStorage;

    /**
     * Генератор уникальных идентификаторов для новых пользователей.
     */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Создаёт новый экземпляр сервиса пользователей.
     *
     * @param userStorage хранилище пользователей, не должен быть null
     */
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Возвращает список всех пользователей в хранилище.
     *
     * @return коллекция всех пользователей, или пустая коллекция, если хранилище пустое
     */
    public Collection<User> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        return users != null ? users : Collections.emptyList();
    }

    /**
     * Возвращает пользователя по указанному идентификатору.
     *
     * @param userId идентификатор пользователя, должен быть положительным
     * @return найденный пользователь
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public User getUserById(Long userId) {
        return getUserOrThrow(userId);
    }

    /**
     * Возвращает пользователя по указанному идентификатору или выбрасывает исключение.
     *
     * @param userId идентификатор пользователя, должен быть положительным
     * @return найденный пользователь
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    private User getUserOrThrow(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + userId + " не найден."));
    }

    /**
     * Добавляет нового пользователя в хранилище.
     * <p>
     * Генерирует уникальный идентификатор для пользователя и сохраняет его в хранилище.
     *
     * @param user объект пользователя для добавления, не должен быть null
     * @return добавленный пользователь с установленным идентификатором
     */
    public User addUser(User user) {
        user.setId(idGenerator.incrementAndGet());
        User addedUser = userStorage.add(user);
        log.info("Добавлен пользователь {} с ID={}", addedUser.getName(), addedUser.getId());
        return addedUser;
    }

    /**
     * Обновляет существующего пользователя в хранилище.
     * <p>
     * Находит пользователя по идентификатору, обновляет его данные и сохраняет изменения.
     * Если пользователь не найден, выбрасывается исключение.
     *
     * @param updUser объект пользователя с обновлёнными данными, не должен быть null, ID обязателен
     * @return обновлённый пользователь
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public User updateUser(User updUser) {
        User existingUser = userStorage.getUserById(updUser.getId())
                .orElseThrow(() -> {
                    log.warn("Попытка обновить несуществующего пользователя с ID={}", updUser.getId());
                    return new UserNotFoundException("Пользователя с ID=" + updUser.getId() + " нет в списке пользователей.");
                });
        existingUser.updateFrom(updUser);
        User updatedUser = userStorage.update(existingUser);
        log.info("Обновлён пользователь {} с ID={}", updatedUser.getName(), updatedUser.getId());
        return updatedUser;
    }

    /**
     * Удаляет пользователя из хранилища по указанному идентификатору.
     *
     * @param userId идентификатор пользователя, который нужно удалить, должен быть положительным
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public void deleteUser(Long userId) {
        userStorage.delete(userId);
        log.info("Удалён пользователь с ID={}", userId);
    }

    /**
     * Добавляет пользователя в список друзей другого пользователя.
     * <p>
     * Создаёт двунаправленную дружескую связь: оба пользователя добавляются в списки друзей
     * друг друга. Если пользователи уже друзья, операция игнорируется.
     *
     * @param userId      идентификатор пользователя, который добавляет друга, должен быть положительным
     * @param newFriendId идентификатор пользователя, которого добавляют в друзья, должен быть положительным
     * @throws UserNotFoundException    если один из пользователей с указанным ID не найден
     * @throws IllegalArgumentException если пользователь пытается добавить в друзья самого себя
     */
    public void addFriend(Long userId, Long newFriendId) {
        if (userId.equals(newFriendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в друзья.");
        }
        User user = getUserOrThrow(userId);
        User newFriend = getUserOrThrow(newFriendId);
        if (user.getFriends().contains(newFriendId)) {
            log.warn("Пользователь с ID={} уже является другом пользователя с ID={}", newFriendId, userId);
            return;
        }
        user.addFriend(newFriendId);
        newFriend.addFriend(userId);
        userStorage.update(user);
        userStorage.update(newFriend);
        log.info("Пользователь с ID={} добавил в друзья пользователя с ID={}", userId, newFriendId);
    }

    /**
     * Удаляет дружескую связь между двумя пользователями.
     * <p>
     * Удаляет двунаправленную дружескую связь: оба пользователя удаляются из списков друзей
     * друг друга.
     *
     * @param userId   идентификатор пользователя, который удаляет друга, должен быть положительным
     * @param friendId идентификатор пользователя, которого удаляют из друзей, должен быть положительным
     * @throws UserNotFoundException если один из пользователей с указанным ID не найден
     */
    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователь с ID={} удалил из друзей пользователя с ID={}", userId, friendId);
    }

    /**
     * Возвращает список общих друзей двух пользователей.
     * <p>
     * Находит пересечение списков друзей двух пользователей и возвращает множество
     * общих друзей.
     *
     * @param userId1 идентификатор первого пользователя, должен быть положительным
     * @param userId2 идентификатор второго пользователя, должен быть положительным
     * @return множество общих друзей
     * @throws UserNotFoundException если один из пользователей с указанным ID не найден
     */
    public Set<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = getUserOrThrow(userId1);
        User user2 = getUserOrThrow(userId2);
        Set<Long> friends1 = user1.getFriends() != null ? user1.getFriends() : Collections.emptySet();
        Set<Long> friends2 = user2.getFriends() != null ? user2.getFriends() : Collections.emptySet();
        Set<Long> commonFriendsIds = new HashSet<>(friends1);
        commonFriendsIds.retainAll(friends2);
        Set<User> commonFriends = commonFriendsIds.stream()
                .map(id -> userStorage.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        log.info("Найдено {} общих друзей для пользователей с ID={} и ID={}", commonFriends.size(), userId1, userId2);
        return commonFriends;
    }

    /**
     * Возвращает список друзей указанного пользователя.
     *
     * @param userId идентификатор пользователя, чьих друзей нужно найти, должен быть положительным
     * @return множество друзей пользователя
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public Set<User> getAllUserFriends(Long userId) {
        User user = getUserOrThrow(userId);
        Set<Long> friends = user.getFriends() != null ? user.getFriends() : Collections.emptySet();
        Set<User> userFriends = friends.stream()
                .map(id -> userStorage.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        log.info("Найдено {} друзей для пользователя с ID={}", userFriends.size(), userId);
        return userFriends;
    }
}