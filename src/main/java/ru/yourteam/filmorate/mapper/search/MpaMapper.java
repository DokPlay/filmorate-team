package ru.yourteam.filmorate.mapper.search;

import ru.yourteam.filmorate.dto.search.MpaDto;
import ru.yourteam.filmorate.model.Mpa;

public class MpaMapper {

    public static MpaDto mapToMpaDto(Mpa mpa) {
        MpaDto mpaDto = new MpaDto();
        if (mpa != null) {
            mpaDto.setId(mpa.getId());
            mpaDto.setName(mpa.getName());
            return mpaDto;
        }
        return null;
    }
}
