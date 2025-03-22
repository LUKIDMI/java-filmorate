package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Фильм должен быть валиден");
    }

    @Test
    void testBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за пустого имени");
    }

    @Test
    void testLongDescription() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за слишком длинного описания");
    }

    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за отрицательной продолжительности");
    }

    @Test
    void testInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за слишком ранней даты релиза");
    }
}


