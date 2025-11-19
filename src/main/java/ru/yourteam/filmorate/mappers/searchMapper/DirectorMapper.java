package ru.yourteam.filmorate.mappers.searchMapper;

import ru.yourteam.filmorate.dto.searchDto.DirectorDto;
import ru.yourteam.filmorate.model.Director;

public class DirectorMapper {

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();

        directorDto.setDirectorId(director.getDirectorId());
        directorDto.setName(director.getName());

        return directorDto;
    }
}
