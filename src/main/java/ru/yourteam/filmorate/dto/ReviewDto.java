package ru.yourteam.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// DTO для передачи данных об отзыве пользователя.
@Data
public class ReviewDto {

    private int reviewId;
    @NotBlank(message = "Название не может быть пустым")
    private String content;
    @Getter
    @Setter
    private boolean positive;
    private int userId;
    private int filmId;
    private int useful;

}

