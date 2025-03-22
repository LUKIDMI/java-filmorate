package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Сервис для управления фильмами.
 * <p>
 * Предоставляет методы для работы с фильмами, включая добавление, обновление, удаление,
 * получение списка фильмов, а также управление лайками. Использует {@link FilmStorage}
 * для хранения данных и {@link UserService} для проверки пользователей.
 */
@Service
@Slf4j
public class FilmService {

    /**
     * Хранилище фильмов, используемое для операций с данными.
     */
    private final FilmStorage filmStorage;

    /**
     * Сервис пользователей, используемый для проверки существования пользователей.
     */
    private final UserService userService;

    /**
     * Генератор уникальных идентификаторов для новых фильмов.
     */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Создаёт новый экземпляр сервиса фильмов.
     *
     * @param filmStorage хранилище фильмов, не должен быть null
     * @param userService сервис пользователей, не должен быть null
     */
    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    /**
     * Возвращает список всех фильмов в хранилище.
     *
     * @return коллекция всех фильмов, или пустая коллекция, если хранилище пустое
     */
    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        return films != null ? films : Collections.emptyList();
    }

    /**
     * Возвращает фильм по указанному идентификатору.
     *
     * @param filmId идентификатор фильма, должен быть положительным
     * @return найденный фильм
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильма с ID=" + filmId + " нет в списке фильмов."));
    }

    /**
     * Добавляет новый фильм в хранилище.
     * <p>
     * Генерирует уникальный идентификатор для фильма и сохраняет его в хранилище.
     *
     * @param film объект фильма для добавления, не должен быть null
     * @return добавленный фильм с установленным идентификатором
     */
    public Film addFilm(Film film) {
        film.setId(idGenerator.incrementAndGet());
        Film addedFilm = filmStorage.add(film);
        log.info("Добавлен фильм {} с ID={}", addedFilm.getName(), addedFilm.getId());
        return addedFilm;
    }

    /**
     * Обновляет существующий фильм в хранилище.
     * <p>
     * Находит фильм по идентификатору, обновляет его данные и сохраняет изменения.
     * Если фильм не найден, выбрасывается исключение.
     *
     * @param updFilm объект фильма с обновлёнными данными, не должен быть null, ID обязателен
     * @return обновлённый фильм
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    public Film updateFilm(Film updFilm) {
        Film existingFilm = filmStorage.getFilmById(updFilm.getId())
                .orElseThrow(() -> {
                    log.warn("Попытка обновить несуществующий фильм с ID={}", updFilm.getId());
                    return new FilmNotFoundException("Фильма с ID=" + updFilm.getId() + " нет в списке фильмов.");
                });
        existingFilm.updateFrom(updFilm);
        Film updatedFilm = filmStorage.update(existingFilm);
        log.info("Обновлён фильм {} с ID={}", updatedFilm.getName(), updatedFilm.getId());
        return updatedFilm;
    }

    /**
     * Удаляет фильм из хранилища по указанному идентификатору.
     *
     * @param filmId идентификатор фильма, который нужно удалить, должен быть положительным
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    public void deleteFilm(Long filmId) {
        log.info("Поступил запрос на удаление фильма с ID={}", filmId);
        filmStorage.delete(filmId);
        log.info("Удалён фильм с ID={}", filmId);
    }

    /**
     * Добавляет лайк фильму от указанного пользователя.
     * <p>
     * Проверяет существование фильма и пользователя. Если пользователь уже поставил лайк,
     * операция игнорируется. В противном случае лайк добавляется, и фильм обновляется в хранилище.
     *
     * @param filmId идентификатор фильма, которому ставится лайк, должен быть положительным
     * @param userId идентификатор пользователя, ставящего лайк, должен быть положительным
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public void addLike(Long filmId, Long userId) {
        log.info("Поступил запрос на добавление лайка фильму с ID={} от пользователя с ID={}", filmId, userId);
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильма с ID=" + filmId + " нет в списке фильмов."));
        userService.getUserById(userId);
        if (film.getLikes().contains(userId)) {
            log.info("Пользователь с ID={} уже поставил лайк фильму с ID={}", userId, filmId);
            return;
        }
        film.addLike(userId);
        filmStorage.update(film);
        log.info("Добавлен лайк фильму с ID={} от пользователя с ID={}", filmId, userId);
    }

    /**
     * Удаляет лайк у фильма от указанного пользователя.
     * <p>
     * Проверяет существование фильма и пользователя. Если пользователь не ставил лайк,
     * операция игнорируется. В противном случае лайк удаляется, и фильм обновляется в хранилище.
     *
     * @param filmId идентификатор фильма, у которого удаляется лайк, должен быть положительным
     * @param userId идентификатор пользователя, чей лайк удаляется, должен быть положительным
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    public void deleteLike(Long filmId, Long userId) {
        log.info("Поступил запрос на удаление лайка у фильма с ID={} от пользователя с ID={}", filmId, userId);
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильма с ID=" + filmId + " нет в списке фильмов."));
        userService.getUserById(userId);
        if (!film.getLikes().contains(userId)) {
            log.info("Пользователь с ID={} не ставил лайк фильму с ID={}", userId, filmId);
            return;
        }
        film.deleteLike(userId);
        filmStorage.update(film);
        log.info("Удалён лайк у фильма с ID={} от пользователя с ID={}", filmId, userId);
    }

    /**
     * Возвращает список самых популярных фильмов, отсортированных по количеству лайков.
     * <p>
     * Фильмы сортируются по убыванию количества лайков, а при равном количестве лайков —
     * по возрастанию идентификатора. Если запрошено больше фильмов, чем есть в хранилище,
     * возвращается доступное количество.
     *
     * @param count количество фильмов для возврата, должно быть положительным
     * @return отсортированное множество фильмов
     * @throws IllegalArgumentException если count меньше 1
     */
    public SortedSet<Film> getMostRatedFilms(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Количество фильмов должно быть положительным");
        }
        Collection<Film> allFilms = filmStorage.getAllFilms();
        if (allFilms == null) {
            log.warn("Хранилище фильмов вернуло null");
            return new TreeSet<>();
        }
        if (count > allFilms.size()) {
            log.info("Запрошено {} фильмов, но доступно только {}", count, allFilms.size());
        }
        Comparator<Film> filmComparator = Comparator
                .comparingInt(Film::getLikesCount)
                .reversed()
                .thenComparing(Film::getId);
        return allFilms.stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toCollection(() -> new TreeSet<>(filmComparator)));
    }
}