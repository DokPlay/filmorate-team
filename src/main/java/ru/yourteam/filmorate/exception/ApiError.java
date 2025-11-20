package ru.yourteam.filmorate.exception;

import java.time.OffsetDateTime;

public class ApiError {
    // commit: расширена модель ошибки для единообразного ответа с HTTP-статусом и путём запроса
    private final int status;
    // commit: фиксирована недостающая информация о коде и сообщении
    private final String error;
    private final String description;
    private final String path;
    private final OffsetDateTime timestamp;

    public ApiError(int status, String error, String description, String path) {
        this.status = status;
        this.error = error;
        this.description = description;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
