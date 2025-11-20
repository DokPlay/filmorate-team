package ru.yourteam.filmorate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dto.CommonFilmDto;

@Component
public class CommonFilmRowMapper implements RowMapper<CommonFilmDto> {
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
