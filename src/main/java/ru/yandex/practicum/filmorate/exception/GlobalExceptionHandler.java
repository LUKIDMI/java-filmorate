package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Обрабатывает различные типы исключений, возникающих в приложении, и возвращает
 * стандартизированные ответы с HTTP-статусами и сообщениями об ошибках.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Ключ для сообщения об ошибке в ответе.
     */
    private static final String ERROR_KEY = "error";

    /**
     * Обрабатывает исключение, когда пользователь не найден.
     *
     * @param e исключение UserNotFoundException, содержащее сообщение об ошибке
     * @return ResponseEntity с телом, содержащим сообщение об ошибке, и статусом 404 (Not Found)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException e) {
        log.warn("Пользователь не найден: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обрабатывает исключение, когда фильм не найден.
     *
     * @param e исключение FilmNotFoundException, содержащее сообщение об ошибке
     * @return ResponseEntity с телом, содержащим сообщение об ошибке, и статусом 404 (Not Found)
     */
    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFilmNotFound(FilmNotFoundException e) {
        log.warn("Фильм не найден: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обрабатывает исключение валидации входных данных.
     * <p>
     * Собирает все ошибки валидации полей и возвращает их в виде пар "поле: сообщение об ошибке".
     *
     * @param e исключение MethodArgumentNotValidException, содержащее ошибки валидации
     * @return ResponseEntity с телом, содержащим ошибки валидации, и статусом 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Ошибка валидации: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Обрабатывает исключение некорректных аргументов.
     *
     * @param e исключение IllegalArgumentException, содержащее сообщение об ошибке
     * @return ResponseEntity с телом, содержащим сообщение об ошибке, и статусом 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Некорректный запрос: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обрабатывает все необработанные исключения.
     * <p>
     * Используется как обработчик по умолчанию для любых непредвиденных ошибок.
     *
     * @param e исключение Exception, содержащее информацию об ошибке
     * @return ResponseEntity с телом, содержащим общее сообщение об ошибке, и статусом 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Произошла непредвиденная ошибка");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}