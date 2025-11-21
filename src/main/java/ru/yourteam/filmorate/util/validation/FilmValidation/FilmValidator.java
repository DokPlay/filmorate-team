package ru.yourteam.filmorate.util.validation.FilmValidation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dal.mappers.reviewRowMappers.FilmRowMapper;
import ru.yourteam.filmorate.exception.NotFoundException;
import ru.yourteam.filmorate.model.Film;

@Component
@RequiredArgsConstructor
public class FilmValidator {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;

    public Film filmValidation(int id) {
        String query = "SELECT * FROM films WHERE film_id = ?";
        try {
            return jdbc.queryForObject(query, filmMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данный фильм отсутствует.");
        }
    }


}
