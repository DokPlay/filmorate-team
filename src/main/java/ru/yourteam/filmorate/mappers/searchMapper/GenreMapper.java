package ru.yourteam.filmorate.mappers.searchMapper;

import ru.yourteam.filmorate.dto.searchDto.GenreDto;
import ru.yourteam.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenreMapper {

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto genreDto = new GenreDto();

        genreDto.setId(genre.getId());
        genreDto.setName(genre.getName());
        return genreDto;
    }

    public static List<GenreDto> mapToGenresDto(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return new ArrayList<>();
        }
        return genres.stream().map(GenreMapper::mapToGenreDto).collect(Collectors.toList());
    }
}
