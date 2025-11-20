package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    private static final String EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?);";

    private final JdbcTemplate jdbcTemplate;

    public void ensureFilmExists(int filmId) {
        if (!existsById(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
    }

    public boolean existsById(int filmId) {
        Boolean exists = jdbcTemplate.queryForObject(EXISTS_QUERY, Boolean.class, filmId);
        return Boolean.TRUE.equals(exists);
    }
}
