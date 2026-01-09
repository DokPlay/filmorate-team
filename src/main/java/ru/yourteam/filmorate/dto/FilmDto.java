package ru.yourteam.filmorate.dto;

import lombok.Data;

/**
 * DTO that represents a film returned from director-related endpoints.
 */
@Data
public final class FilmDto {
    private Long id;
    private String name;
    private String description;
    private Integer releaseYear;
    private Integer duration;
}
