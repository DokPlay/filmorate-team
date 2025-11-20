package ru.yourteam.filmorate.exception;

import java.time.OffsetDateTime;

public class ApiError {
    // commit: введено поле code, чтобы явно передавать HTTP-статус в читаемом виде
    private final int code;
    // commit: сообщение стало коротким и понятным клиенту, без технических деталей
    private final String message;
    // commit: details содержит расшифровку ошибки/валидации для дебага на стороне клиента
    private final String details;
    private final String path;
    private final OffsetDateTime timestamp;

    public ApiError(int code, String message, String details, String path) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
