package ru.yourteam.filmorate.mappers.searchMapper;

import ru.yourteam.filmorate.dto.searchDto.DirectorDto;
import ru.yourteam.filmorate.model.Director;

/**
 * Маппер доменной модели Director в DTO для поиска.
 */
public final class DirectorMapper {

    private DirectorMapper() {
        // утилитарный класс
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }
}
