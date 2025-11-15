package ru.yourteam.filmorate.service;

public enum SortMode {
    LIKES, YEAR;

    public static SortMode from(String raw) {
        if (raw == null) throw new IllegalArgumentException("sort is required");
        switch (raw.toLowerCase()) {
            case "likes": return LIKES;
            case "year":  return YEAR;
            default: throw new IllegalArgumentException("sort must be 'likes' or 'year'");
        }
    }
}
