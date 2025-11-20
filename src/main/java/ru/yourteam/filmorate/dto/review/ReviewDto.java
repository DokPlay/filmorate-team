package ru.yourteam.filmorate.dto.review;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// DTO для передачи данных об отзыве пользователя.
@Data
public class ReviewDto {

    @Min(value = 0, message = "Идентификатор отзыва не может быть отрицательным")
    private int reviewId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @Getter
    @Setter
    private boolean positive;

    @NotNull(message = "Не указан пользователь")
    @Positive(message = "Идентификатор пользователя должен быть положительным")
    private Integer userId;

    @NotNull(message = "Не указан фильм")
    @Positive(message = "Идентификатор фильма должен быть положительным")
    private Integer filmId;

    @Min(value = 0, message = "Полезность отзыва не может быть отрицательной")
    private int useful;

}

