package ru.yourteam.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int mpaId;
    private Mpa mpa;
    private List<Genre> genres;
    private Director director;
}
