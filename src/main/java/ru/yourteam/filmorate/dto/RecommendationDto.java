package ru.yourteam.filmorate.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * DTO для передачи рекомендаций фильмов пользователю.
 */
@Data
public class RecommendationDto {

    /**
     * Идентификатор фильма.
     */
    private int filmId;

    /**
     * Название фильма.
     */
    private String name;

    /**
     * Описание фильма.
     */
    private String description;

    /**
     * Дата выхода фильма.
     */
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах.
     */
    private int duration;

    /**
     * Идентификатор рейтинга MPA.
     */
    private int mpaId;

    /**
     * Внутренний "вес" рекомендации:
     * чем выше, тем ближе фильм к вкусам пользователя (основано на числе похожих пользователей,
     * поставивших лайк этому фильму).
     */
    private int relevanceScore;
}
