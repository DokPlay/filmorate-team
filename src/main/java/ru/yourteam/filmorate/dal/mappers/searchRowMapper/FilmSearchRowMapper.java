package ru.yourteam.filmorate.dal.mappers.searchRowMapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class FilmSearchRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Mpa mpa = new Mpa();

        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);

        Director director = new Director();
        director.setId(resultSet.getLong("director_id"));
        director.setName(resultSet.getString("director_name"));

        // Оборачиваем режиссера в коллекцию, потому что в сущности хранится ManyToMany
        film.setDirectors(Collections.singletonList(director));
        film.setGenres(new ArrayList<>());
        return film;
    }
}
