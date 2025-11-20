package ru.yourteam.filmorate.dto;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommonFilmDtoDto implements RowMapper<CommonFilmDto> {
    @Override
    public CommonFilmDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        CommonFilmDto commonFilmDto = new CommonFilmDto();
        commonFilmDto.setId(resultSet.getLong("id"));
        commonFilmDto.setName(resultSet.getString("name"));
        commonFilmDto.setLikes(resultSet.getInt("likes"));
        commonFilmDto.setReleaseYear(resultSet.getInt("release_year"));
        return commonFilmDto;
    }
}
