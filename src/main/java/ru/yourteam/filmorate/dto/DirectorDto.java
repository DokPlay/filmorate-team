package ru.yourteam.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO режиссёра, используемое для входящих/исходящих данных контроллеров.
 */
@Data
public class DirectorDto {

    @PositiveOrZero(message = "Идентификатор режиссёра не может быть отрицательным")
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    @Size(max = 255, message = "Имя режиссёра не должно превышать 255 символов")
    private String name;
}
