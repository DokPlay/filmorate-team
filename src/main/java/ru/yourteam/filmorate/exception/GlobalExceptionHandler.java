package ru.yourteam.filmorate.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        // commit: унифицирован возврат 404 с понятным кодом и деталями, вместо разрозненных ответов
        log.warn("Resource not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            IllegalArgumentException.class, ValidationException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest request) {
        // commit: приведена валидация к HTTP 400 с детализированным сообщением, агрегируем все ошибки
        String details = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException manve && manve.getBindingResult().hasErrors()) {
            details = manve.getBindingResult().getAllErrors().stream()
                    .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : error.toString())
                    .distinct()
                    .reduce((left, right) -> left + "; " + right)
                    .orElse("Validation failed");
        }
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(Exception ex, HttpServletRequest request) {
        // commit: скрыты детали БД и возвращён предсказуемый ответ 409 с безопасным описанием
        log.warn("Constraint violation: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "Constraint conflict", "Duplicate key or constraint violation", request);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternal(InternalServerException ex, HttpServletRequest request) {
        // commit: исключения уровня сервиса теперь отдаются с кодом 500 и пояснением для клиента
        log.error("Internal error: {}", ex.getMessage());
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        // commit: добавлено fallback-обработчик, чтобы возвращать структурированный JSON вместо stacktrace
        log.error("Unexpected error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", "The server could not process the request", request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, String details, HttpServletRequest request) {
        // commit: унифицированная фабрика ошибок возвращает однообразный JSON с кодом, сообщением и деталями
        ApiError body = new ApiError(status.value(), message, details, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
