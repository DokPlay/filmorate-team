package ru.yourteam.filmorate.exception;

/**
 * Исключение для ситуаций, когда сущность не найдена.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
