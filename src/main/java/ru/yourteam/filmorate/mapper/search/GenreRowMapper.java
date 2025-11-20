package ru.yourteam.filmorate.mapper.search;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        try {
            genre.setFilmId(resultSet.getInt("film_id"));
            genre.setId(resultSet.getInt("genre_id"));
            genre.setName(resultSet.getString("genre_name"));
        } catch (SQLException e) {
            genre.setId(resultSet.getInt("genre_id"));
            genre.setName(resultSet.getString("genre_name"));
        }
        return genre;
    }
}
