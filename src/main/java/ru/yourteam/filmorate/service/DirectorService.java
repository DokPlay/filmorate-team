// Сервисный слой для управления режиссёрами и их фильмами.
package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director create(Director dto);
    Director update(Director dto);
    Director getById(long id);
    List<Director> getAll();
    void deleteById(long id);

    // TODO
    //List<FilmDto> getFilmsByDirector(long directorId, SortMode sort); // SortMode { LIKES, YEAR }
}
