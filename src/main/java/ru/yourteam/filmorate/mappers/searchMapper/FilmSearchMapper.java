package ru.yourteam.filmorate.mappers.searchMapper;

import ru.yourteam.filmorate.dto.searchDto.FilmSearchDto;
import ru.yourteam.filmorate.model.Film;

public class FilmSearchMapper {

    public static FilmSearchDto mapToFilmSearchDto(Film film) {
        FilmSearchDto filmDto = new FilmSearchDto();

        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(MpaMapper.mapToMpaDto(film.getMpa()));
        filmDto.setGenres(GenreMapper.mapToGenresDto(film.getGenres()));
        filmDto.setDirectorDto(DirectorMapper.mapToDirectorDto(film.getDirector()));

        return filmDto;
    }
}
