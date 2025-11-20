// DTO для описания фильмов, общих у нескольких пользователей.
package ru.yourteam.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class CommonFilmDto {
    private long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    private int likes;

    private int releaseYear;

}
