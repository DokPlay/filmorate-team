package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.dto.CommonFilmDto;

import java.util.List;

/**
 * Сервисный слой для расчёта общих фильмов между пользователями.
 */
public interface CommonFilmService {
    List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId);
}
