// Сервисный слой для расчёта общих фильмов между пользователями.
package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.dto.CommonFilmDto;
import java.util.List;

public interface CommonFilmService {
    List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId);
}
