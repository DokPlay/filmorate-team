package ru.yourteam.filmorate.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException ex) { return new ApiError("NOT_FOUND", ex.getMessage()); }

    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class, ValidationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(Exception ex) { return new ApiError("BAD_REQUEST", ex.getMessage()); }

    // 409, если конфликт (например, UNIQUE name)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(Exception ex) { return new ApiError("CONFLICT", "Duplicate/constraint violation"); }
}
