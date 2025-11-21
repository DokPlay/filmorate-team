// Сервисный слой для управления режиссёрами и их фильмами.
package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.dto.FilmDto;

import java.util.List;

public interface DirectorService {
    Director create(Director dto);
    Director update(Director dto);
    Director getById(long id);
    List<Director> getAll();
    void deleteById(long id);

    // Получение фильмов режиссера с сортировкой и постраничным доступом
    List<FilmDto> getFilmsByDirector(long directorId, SortMode sortMode, int limit, int offset);
}
