package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Set;

@RestController
@Validated
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Возвращает список всех фильмов в хранилище.
     *
     * @return коллекция всех фильмов
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов, количество: {}", filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }

    /**
     * Добавляет новый фильм в хранилище.
     *
     * @param film объект фильма, содержащий данные для добавления, не должен быть null
     * @return ResponseEntity с добавленным фильмом и статусом 201 (Created)
     * @throws jakarta.validation.ConstraintViolationException если данные фильма не прошли валидацию
     * @throws IllegalArgumentException если фильм с таким ID уже существует
     */
    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody @Valid Film film) {
        log.info("Получен запрос на добавление фильма {}", film.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(film));
    }

    /**
     * Обновляет существующий фильм в хранилище на основе предоставленных данных.
     * <p>
     * Метод принимает объект фильма с обновлёнными данными и заменяет существующий фильм с таким же ID.
     * Если фильм с указанным ID не найден, будет выброшено исключение.
     *
     * @param updFilm объект фильма с обновлёнными данными, не должен быть null, ID обязателен
     * @return ResponseEntity с обновлённым фильмом и статусом 200 (OK)
     * @throws jakarta.validation.ConstraintViolationException если данные фильма не прошли валидацию
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @NotNull @Valid Film updFilm) {
        log.info("Получен запрос на обновление фильма с ID={}", updFilm.getId());
        return ResponseEntity.ok(filmService.updateFilm(updFilm));
    }

    /**
     * Возвращает фильм по указанному идентификатору.
     *
     * @param id идентификатор фильма, должен быть положительным
     * @return ResponseEntity с найденным фильмом и статусом 200 (OK)
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        log.info("Получен запрос на фильм с ID={}", id);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    /**
     * Удаляет фильм из хранилища по указанному идентификатору.
     * <p>
     * Метод удаляет фильм с указанным ID из хранилища. Если фильм не найден,
     * будет выброшено исключение, и клиент получит статус 404 (Not Found).
     *
     * @param id идентификатор фильма, который необходимо удалить, должен быть положительным
     * @return ResponseEntity с пустым телом и статусом 204 (No Content)
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        log.info("Получен запрос на удаление фильма с ID={}", id);
        filmService.deleteFilm(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Добавляет лайк фильму от указанного пользователя.
     * <p>
     * Метод добавляет лайк к фильму с указанным ID от пользователя с указанным ID.
     * Если фильм или пользователь не найдены, будет выброшено исключение.
     *
     * @param filmId идентификатор фильма, которому ставится лайк, должен быть положительным
     * @param userId идентификатор пользователя, который ставит лайк, должен быть положительным
     * @return ResponseEntity с обновлённым фильмом (с новым лайком) и статусом 200 (OK)
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Получен запрос на установку лайка фильму с ID={} от пользователя ID={}", filmId, userId);
        filmService.addLike(filmId, userId);
        Film updatedFilm = filmService.getFilmById(filmId);
        return ResponseEntity.ok(updatedFilm);
    }

    /**
     * Удаляет лайк у фильма от указанного пользователя.
     *
     * @param filmId идентификатор фильма, у которого удаляется лайк, должен быть положительным
     * @param userId идентификатор пользователя, чей лайк удаляется, должен быть положительным
     * @return ResponseEntity с пустым телом и статусом 204 (No Content)
     * @throws FilmNotFoundException если фильм с указанным ID не найден
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка у фильма с ID={} от пользователя ID={}", filmId, userId);
        filmService.deleteLike(filmId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Возвращает список самых популярных фильмов, отсортированных по количеству лайков.
     *
     * @param count количество фильмов для возврата, должно быть положительным
     * @return ResponseEntity с набором фильмов, отсортированных по убыванию количества лайков, и статусом 200 (OK)
     * @throws IllegalArgumentException если count меньше 1
     */
    @GetMapping("/popular")
    public ResponseEntity<Set<Film>> getMostRatedFilms(@RequestParam(defaultValue = "10") @Min(1) int count) {
        log.info("Получен запрос на топ-{} фильмов по количеству лайков", count);
        Set<Film> films = filmService.getMostRatedFilms(count);
        log.info("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }
}