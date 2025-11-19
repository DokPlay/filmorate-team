package ru.yourteam.filmorate.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CommonFilmRowMapper implements RowMapper<CommonFilmDto> {
    @Override
    public CommonFilmDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        CommonFilmDto commonFilmDto = new CommonFilmDto();
        commonFilmDto.setId(resultSet.getLong("id"));
        commonFilmDto.setName(resultSet.getString("name"));
        return commonFilmDto;
    }
}
