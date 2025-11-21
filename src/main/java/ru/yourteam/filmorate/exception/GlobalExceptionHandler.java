package ru.yourteam.filmorate.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Добавлено: единообразное оформление 404 и логирование
    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request);
    }

    // Добавлено: подробная сборка сообщений валидации для 400 ответа
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            IllegalArgumentException.class, ValidationException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest request) {
        String detail = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException manve && manve.getBindingResult().hasErrors()) {
            detail = manve.getBindingResult().getFieldErrors().stream()
                    .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof ConstraintViolationException cve) {
            detail = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
        }
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", detail, request);
    }

    // Добавлено: чёткое описание конфликтов БД без утечки внутренних деталей
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(Exception ex, HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "Resource conflict", "Duplicate or constraint violation", request);
    }

    // Добавлено: специальный проход для внутренних ошибок домена
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternal(InternalServerException ex, HttpServletRequest request) {
        log.error("Internal error: {}", ex.getMessage());
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", ex.getMessage(), request);
    }

    // Добавлено: страховка на прочие исключения с минимальным раскрытием деталей
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.getMessage(), request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, String detail, HttpServletRequest request) {
        ApiError body = new ApiError(status, message, detail, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
