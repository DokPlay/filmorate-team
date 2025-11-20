// Сервисный слой для выборки популярных фильмов по фильтрам.
package ru.yourteam.filmorate.service;

import java.util.List;
import ru.yourteam.filmorate.dto.PopularFilmDto;

public interface PopularFilmService {

    /**
     * Возвращает топ-N популярных фильмов,
     * отфильтрованных по жанру и/или году.
     *
     * @param count  максимальное количество фильмов (если <= 0, используется дефолтное значение).
     * @param genreId идентификатор жанра, если нужно фильтровать по жанру, иначе null.
     * @param year    год релиза, если нужно фильтровать по году, иначе null.
     */
    List<PopularFilmDto> getMostPopular(int count, Integer genreId, Integer year);
}
