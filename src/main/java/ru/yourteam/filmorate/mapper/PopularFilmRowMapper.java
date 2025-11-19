package ru.yourteam.filmorate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dto.PopularFilmDto;

@Component
public class PopularFilmRowMapper implements RowMapper<PopularFilmDto> {

    @Override
    public PopularFilmDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PopularFilmDto dto = new PopularFilmDto();
        dto.setFilmId(rs.getInt("film_id"));
        dto.setName(rs.getString("film_name"));
        dto.setDescription(rs.getString("description"));
        dto.setReleaseDate(rs.getDate("release_date").toLocalDate());
        dto.setDuration(rs.getInt("duration"));
        dto.setMpaId(rs.getInt("mpa_id"));
        dto.setLikesCount(rs.getInt("likes_count"));
        return dto;
    }
}
//Здесь мы опираемся на структуру таблиц и маппинга из ветки add-reviews (FilmRowMapper, Film модель). 