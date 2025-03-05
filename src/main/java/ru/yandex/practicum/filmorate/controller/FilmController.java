package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@Validated
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов. Количество: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {} (ID={})", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @NotNull @Valid Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка добавить фильм с несуществующим ID={}", newFilm.getId());
            throw new IllegalArgumentException("Фильма с id=" + newFilm.getId() + " нет в списке фильмов.");
        }
        Film oldFilm = films.get(newFilm.getId());

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Информация о фильме {} (ID={}) обновлена.", oldFilm.getName(), oldFilm.getId());
        return oldFilm;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
