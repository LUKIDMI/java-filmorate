package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных, представляющая фильм.
 * <p>
 * Содержит информацию о фильме, такую как название, описание, дата релиза, продолжительность
 * и список лайков от пользователей. Используется для хранения и обработки данных о фильмах
 * в приложении.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    /**
     * Идентификатор фильма, уникальный для каждого фильма.
     */
    Long id;

    /**
     * Название фильма, не может быть пустым.
     */
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;

    /**
     * Описание фильма, максимальная длина — 200 символов.
     */
    @Size(max = 200, message = "Размер описания не должен превышать 200 символов.")
    String description;

    /**
     * Дата релиза фильма, не может быть null и должна быть не ранее 28 декабря 1895 года.
     */
    @NotNull(message = "Дата релиза не может быть null.")
    LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах, должна быть положительной или равной нулю.
     */
    @NotNull(message = "Продолжительность фильма не может быть null.")
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительным числом.")
    int duration;

    /**
     * Множество идентификаторов пользователей, поставивших лайк фильму.
     */
    Set<Long> likes;

    /**
     * Создаёт новый объект фильма с пустым множеством лайков.
     * <p>
     * Инициализирует поле likes пустым HashSet, чтобы избежать
     * NullPointerException при работе с лайками.
     */
    public Film() {
        this.likes = new HashSet<>();
    }

    /**
     * Проверяет, что дата релиза фильма не ранее 28 декабря 1895 года.
     * <p>
     * Используется для валидации поля releaseDate. Если дата релиза null
     * или раньше указанной даты, возвращает false.
     *
     * @return true, если дата релиза валидна, иначе false
     */
    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года.")
    public boolean isReleaseDateValid() {
        return releaseDate != null && !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    /**
     * Обновляет текущий фильм на основе данных из другого объекта фильма.
     * <p>
     * Обновляет поля текущего объекта, если соответствующие поля в other
     * не равны null. Поле duration обновляется только если значение
     * в other не равно 0.
     *
     * @param other объект фильма, содержащий обновлённые данные, не должен быть null
     * @throws IllegalArgumentException если other равен null
     */
    public void updateFrom(Film other) {
        if (other == null) {
            throw new IllegalArgumentException("Объект для обновления не может быть null");
        }
        if (other.getName() != null) {
            this.setName(other.getName());
        }
        if (other.getDescription() != null) {
            this.setDescription(other.getDescription());
        }
        if (other.getReleaseDate() != null) {
            this.setReleaseDate(other.getReleaseDate());
        }
        if (other.getDuration() != 0) {
            this.setDuration(other.getDuration());
        }
    }

    /**
     * Возвращает количество лайков, поставленных фильму.
     *
     * @return количество лайков
     */
    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    /**
     * Добавляет лайк фильму от указанного пользователя.
     *
     * @param userId идентификатор пользователя, ставящего лайк, не должен быть null
     * @throws IllegalArgumentException если userId равен null
     */
    public void addLike(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        if (likes == null) {
            this.likes = new HashSet<>();
        }
        likes.add(userId);
    }

    /**
     * Удаляет лайк у фильма от указанного пользователя.
     *
     * @param userId идентификатор пользователя, чей лайк нужно удалить, не должен быть null
     * @throws IllegalArgumentException если userId равен null
     */
    public void deleteLike(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        if (likes != null) {
            likes.remove(userId);
        }
    }
}