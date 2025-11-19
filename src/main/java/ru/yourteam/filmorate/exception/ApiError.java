package ru.yourteam.filmorate.exception;

public class ApiError {
    private final String error;
    private final String description;

    public ApiError(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
