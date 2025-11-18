// DTO для представления популярного фильма с дополнительными атрибутами.
package ru.yourteam.filmorate.dto;

import java.time.LocalDate;
import lombok.Data;

// DTO для представления популярного фильма с дополнительными атрибутами.
@Data
public class PopularFilmDto {

    /** Идентификатор фильма. */
    private int filmId;

    /** Название фильма. */
    private String name;

    /** Описание фильма. */
    private String description;

    /** Дата релиза. */
    private LocalDate releaseDate;

    /** Длительность в минутах. */
    private int duration;

    /** Идентификатор рейтинга MPA. */
    private int mpaId;

    /** Количество лайков (уникальных пользователей). */
    private int likesCount;
}

//RecommendationDto уже возвращает похожий набор полей + свой рейтинг,
// поэтому здесь повторяем структуру и добавляем likesCount вместо relevanceScore
//(ориентир на PR add-recommendations — структура RecommendationDto).