package ru.yourteam.filmorate.dto.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmSearchDto {
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длинна описания не более 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private MpaDto mpa;
    private List<GenreDto> genres;
    private DirectorDto directorDto;
}
