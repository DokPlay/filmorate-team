package ru.yourteam.filmorate.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class ApiError {
    // Добавлено: единый контракт ответа об ошибке с кодом статуса, сообщением и деталями запроса
    private final int status;
    private final String code;
    private final String message;
    private final String detail;
    private final String path;
    private final OffsetDateTime timestamp;

    public ApiError(HttpStatus status, String message, String detail, String path) {
        this.status = status.value();
        this.code = status.name();
        this.message = message;
        this.detail = detail;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
