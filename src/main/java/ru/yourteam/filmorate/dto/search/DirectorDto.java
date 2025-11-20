package ru.yourteam.filmorate.dto.search;

import lombok.Data;

/**
 * DTO режиссёра, используемое в ответах поиска фильмов.
 */
@Data
public class DirectorDto {

    private long id;

    private String name;
}
