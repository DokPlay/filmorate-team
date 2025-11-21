package ru.yourteam.filmorate.service;

public enum SortMode {
    LIKES, YEAR;

    public static SortMode from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("sort is required");
        }
        String normalized = raw.trim().toLowerCase();
        return switch (normalized) {
            case "likes" -> LIKES;
            case "year" -> YEAR;
            default -> throw new IllegalArgumentException("sort must be 'likes' or 'year'");
        };
    }
}
