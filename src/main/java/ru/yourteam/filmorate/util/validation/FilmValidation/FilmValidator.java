package ru.yourteam.filmorate.util.validation.FilmValidation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dal.mappers.reviewRowMappers.FilmRowMapper;
import ru.yourteam.filmorate.exceptions.NotFoundException;
import ru.yourteam.filmorate.model.Film;

@Component
public class FilmValidator {

    JdbcTemplate jdbc;
    FilmRowMapper filmMapper;

    public Film filmValidation(int id) {
        String query = "SELECT * FROM films WHERE film_id = ?";
        try {
            return jdbc.queryForObject(query, filmMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данный фильм отсутствует.");
        }
    }


}
