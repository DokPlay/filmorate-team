package ru.yourteam.filmorate.service;

import ru.yourteam.filmorate.dto.CommonFilmDto;

import java.util.List;

/**
 * Сервисный слой для расчёта общих фильмов между пользователями.
 * <p>
 * Принимает идентификаторы двух пользователей, проверяет валидность входных данных на уровне
 * сервисов и предоставляет коллекцию DTO с отсортированными по популярности совпадениями.
 */
public interface CommonFilmService {
    List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId);
}
