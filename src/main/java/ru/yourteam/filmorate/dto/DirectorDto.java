// DTO для передачи сведений о режиссёре и связанных фильмах.
package ru.yourteam.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class DirectorDto {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;
}
