package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

/**
 * Реализация хранилища пользователей в памяти.
 * <p>
 * Хранит пользователей в HashMap, где ключом является идентификатор пользователя,
 * а значением — объект пользователя. Предоставляет методы для добавления, обновления,
 * удаления и получения пользователей.
 */
@Component
public class InMemoryUserStorage implements UserStorage {

    /**
     * Карта для хранения пользователей, где ключ — идентификатор пользователя, а значение — объект пользователя.
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Возвращает список всех пользователей в хранилище.
     * <p>
     * Возвращает неизменяемую коллекцию, чтобы предотвратить модификацию данных
     * извне.
     *
     * @return коллекция всех пользователей, может быть пустой, если пользователей нет
     */
    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    /**
     * Возвращает пользователя по указанному идентификатору.
     *
     * @param userId идентификатор пользователя, не должен быть null
     * @return Optional, содержащий найденного пользователя, или пустой Optional, если пользователь не найден
     * @throws IllegalArgumentException если userId равен null
     */
    @Override
    public Optional<User> getUserById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * Добавляет нового пользователя в хранилище.
     * <p>
     * Проверяет, что пользователь с таким идентификатором ещё не существует.
     * Если пользователь уже существует, выбрасывается исключение.
     *
     * @param user объект пользователя для добавления, не должен быть null
     * @return добавленный пользователь
     * @throws IllegalArgumentException если user равен null или пользователь с таким ID уже существует
     */
    @Override
    public User add(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        if (users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Пользователь с ID=" + user.getId() + " уже существует в хранилище");
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Обновляет существующего пользователя в хранилище.
     * <p>
     * Проверяет, что пользователь с указанным идентификатором существует.
     * Если пользователь не найден, выбрасывается исключение.
     *
     * @param user объект пользователя с обновлёнными данными, не должен быть null
     * @return обновлённый пользователь
     * @throws IllegalArgumentException если user равен null или идентификатор пользователя равен null
     * @throws UserNotFoundException    если пользователь с указанным ID не найден
     */
    @Override
    public User update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с ID=" + user.getId() + " не найден для обновления");
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Удаляет пользователя из хранилища по указанному идентификатору.
     * <p>
     * Проверяет, что пользователь с указанным идентификатором существует.
     * Если пользователь не найден, выбрасывается исключение.
     *
     * @param id идентификатор пользователя, который нужно удалить, не должен быть null
     * @throws IllegalArgumentException если id равен null
     * @throws UserNotFoundException    если пользователь с указанным ID не найден
     */
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с ID=" + id + " не найден для удаления");
        }
        users.remove(id);
    }
}