package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

/**
 * Реализация хранилища фильмов в памяти.
 * <p>
 * Хранит фильмы в HashMap, где ключом является идентификатор фильма,
 * а значением — объект фильма. Предоставляет методы для добавления, обновления,
 * удаления и получения фильмов.
 */
@Component
public class InMemoryFilmStorage implements FilmStorage {

    /**
     * Карта для хранения фильмов, где ключ — идентификатор фильма, а значение — объект фильма.
     */
    private final Map<Long, Film> films = new HashMap<>();

    /**
     * Возвращает список всех фильмов в хранилище.
     * <p>
     * Возвращает неизменяемую коллекцию, чтобы предотвратить модификацию данных
     * извне.
     *
     * @return коллекция всех фильмов, может быть пустой, если фильмов нет
     */
    @Override
    public Collection<Film> getAllFilms() {
        return Collections.unmodifiableCollection(films.values());
    }

    /**
     * Возвращает фильм по указанному идентификатору.
     *
     * @param filmId идентификатор фильма, не должен быть null
     * @return Optional, содержащий найденный фильм, или пустой Optional, если фильм не найден
     * @throws IllegalArgumentException если filmId равен null
     */
    @Override
    public Optional<Film> getFilmById(Long filmId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Идентификатор фильма не может быть null");
        }
        return Optional.ofNullable(films.get(filmId));
    }

    /**
     * Метод валидации фильма
     * <p>
     * Проверяет, что фильм не null и что идентификатор не равен null, иначе выбрасывается исключение.
     *
     * @param film объект фильма для валидации
     * @throws IllegalArgumentException если film равен null или идентификатор равен null.
     */
    private void validateFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Фильм не может быть null.");
        }
        if (film.getId() == null) {
            throw new IllegalArgumentException("Идентификатор фильма не может быть null.");
        }
    }

    /**
     * Добавляет новый фильм в хранилище.
     * <p>
     * Проверяет, что фильм с таким идентификатором ещё не существует.
     * Если фильм уже существует, выбрасывается исключение.
     *
     * @param film объект фильма для добавления, не должен быть null
     * @return добавленный фильм
     * @throws IllegalArgumentException если film равен null или фильм с таким ID уже существует
     */
    @Override
    public Film add(Film film) {
        validateFilm(film);

        if (films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Фильм с ID=" + film.getId() + " уже существует в хранилище");
        }
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Обновляет существующий фильм в хранилище.
     * <p>
     * Проверяет, что фильм с указанным идентификатором существует.
     * Если фильм не найден, выбрасывается исключение.
     *
     * @param film объект фильма с обновлёнными данными, не должен быть null
     * @return обновлённый фильм
     * @throws IllegalArgumentException если film равен null или идентификатор фильма равен null
     * @throws FilmNotFoundException    если фильм с указанным ID не найден
     */
    @Override
    public Film update(Film film) {
        validateFilm(film);

        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с ID=" + film.getId() + " не найден для обновления");
        }
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Удаляет фильм из хранилища по указанному идентификатору.
     * <p>
     * Проверяет, что фильм с указанным идентификатором существует.
     * Если фильм не найден, выбрасывается исключение.
     *
     * @param id идентификатор фильма, который нужно удалить, не должен быть null
     * @throws IllegalArgumentException если id равен null
     * @throws FilmNotFoundException    если фильм с указанным ID не найден
     */
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Идентификатор фильма не может быть null");
        }
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с ID=" + id + " не найден для удаления");
        }
        films.remove(id);
    }
}