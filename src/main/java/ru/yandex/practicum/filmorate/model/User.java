package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных, представляющая пользователя.
 * <p>
 * Содержит информацию о пользователе, такую как email, логин, имя, дата рождения
 * и список друзей. Используется для хранения и обработки данных о пользователях
 * в приложении.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    /**
     * Идентификатор пользователя, уникальный для каждого пользователя.
     */
    Long id;

    /**
     * Email пользователя, должен быть валидным и не пустым.
     */
    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Некорректный формат email.")
    String email;

    /**
     * Логин пользователя, не должен быть пустым и не должен содержать пробелы.
     */
    @NotBlank(message = "Поле с логином не должно быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы.")
    String login;

    /**
     * Имя пользователя, может быть пустым (в таком случае используется логин).
     */
    String name;

    /**
     * Дата рождения пользователя, должна быть в прошлом и не null.
     */
    @NotNull(message = "Дата рождения не может быть null.")
    @Past(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;

    /**
     * Множество идентификаторов друзей пользователя.
     */
    Set<Long> friends;

    /**
     * Создаёт новый объект пользователя с пустым множеством друзей.
     * <p>
     * Инициализирует поле friends пустым HashSet, чтобы избежать
     * NullPointerException при работе с друзьями.
     */
    public User() {
        this.friends = new HashSet<>();
    }

    /**
     * Обновляет текущего пользователя на основе данных из другого объекта пользователя.
     * <p>
     * Обновляет поля текущего объекта, если соответствующие поля в other
     * не равны null. Если имя в other пустое или null,
     * используется текущий логин пользователя.
     *
     * @param other объект пользователя, содержащий обновлённые данные, не должен быть null
     * @throws IllegalArgumentException если other равен null
     */
    public void updateFrom(User other) {
        if (other == null) {
            throw new IllegalArgumentException("Объект для обновления не может быть null");
        }
        if (other.getEmail() != null) {
            this.setEmail(other.getEmail());
        }
        if (other.getLogin() != null) {
            this.setLogin(other.getLogin());
        }
        if (other.getBirthday() != null) {
            this.setBirthday(other.getBirthday());
        }
        if (other.getName() == null || other.getName().isBlank()) {
            if (this.getLogin() != null) {
                this.setName(this.getLogin());
            }
        } else {
            this.setName(other.getName());
        }
    }

    /**
     * Добавляет пользователя в список друзей.
     *
     * @param id идентификатор пользователя, которого нужно добавить в друзья, не должен быть null
     * @throws IllegalArgumentException если id равен null
     */
    public void addFriend(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Идентификатор друга не может быть null");
        }
        if (friends == null) {
            this.friends = new HashSet<>();
        }
        this.friends.add(id);
    }

    /**
     * Удаляет пользователя из списка друзей.
     *
     * @param id идентификатор пользователя, которого нужно удалить из друзей, не должен быть null
     * @throws IllegalArgumentException если id равен null
     */
    public void deleteFriend(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Идентификатор друга не может быть null");
        }
        if (friends != null) {
            this.friends.remove(id);
        }
    }
}