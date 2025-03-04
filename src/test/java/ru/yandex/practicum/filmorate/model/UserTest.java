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

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("i@lukidmi.ru");
        user.setLogin("Логин");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Пользователь должен быть валиден");
    }

    @Test
    void testBlankEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("Логин");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за пустого email");
    }

    @Test
    void testInvalidEmailFormat() {
        User user = new User();
        user.setEmail("ыыыыы");
        user.setLogin("Логин");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за некорректного формата email");
    }

    @Test
    void testBlankLogin() {
        User user = new User();
        user.setEmail("i@lukidmi.ru");
        user.setLogin("");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за пустого логина");
    }

    @Test
    void testLoginWithSpaces() {
        User user = new User();
        user.setEmail("i@lukidmi.ru");
        user.setLogin("Логин Логин");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за пробелов в логине");
    }

    @Test
    void testFutureBirthday() {
        User user = new User();
        user.setEmail("i@lukidmi.ru");
        user.setLogin("Логин");
        user.setName("Ирек Валиев");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации из-за даты рождения в будущем");
    }
}