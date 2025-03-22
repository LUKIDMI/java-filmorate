package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс хранилища фильмов.
 * <p>
 * Определяет контракт для работы с хранилищем фильмов, включая методы для добавления,
 * обновления, удаления и получения фильмов.
 */
public interface FilmStorage {

    /**
     * Возвращает список всех фильмов в хранилище.
     *
     * @return коллекция всех фильмов, может быть пустой, если фильмов нет
     */
    Collection<Film> getAllFilms();

    /**
     * Возвращает фильм по указанному идентификатору.
     *
     * @param filmId идентификатор фильма
     * @return Optional, содержащий найденный фильм, или пустой Optional, если фильм не найден
     */
    Optional<Film> getFilmById(Long filmId);

    /**
     * Добавляет новый фильм в хранилище.
     *
     * @param film объект фильма для добавления, не должен быть null
     * @return добавленный фильм
     */
    Film add(Film film);

    /**
     * Обновляет существующий фильм в хранилище.
     *
     * @param film объект фильма с обновлёнными данными, не должен быть null
     * @return обновлённый фильм
     */
    Film update(Film film);

    /**
     * Удаляет фильм из хранилища по указанному идентификатору.
     *
     * @param id идентификатор фильма, который нужно удалить
     */
    void delete(Long id);
}
