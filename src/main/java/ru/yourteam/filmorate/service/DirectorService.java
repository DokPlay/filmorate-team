// Сервисный слой для управления режиссёрами и их фильмами.
package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.dto.DirectorDto;

import java.util.List;

public interface  DirectorService {
    DirectorDto create(DirectorDto dto);
    DirectorDto update(DirectorDto dto);
    DirectorDto getById(long id);
    List<DirectorDto> getAll();
    void deleteById(long id);

    // TODO
    //List<FilmDto> getFilmsByDirector(long directorId, SortMode sort); // SortMode { LIKES, YEAR }
}
