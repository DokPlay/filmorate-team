package ru.yourteam.filmorate.mapper.review;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpaId(resultSet.getInt("mpa_id"));
        return film;
    }
}
