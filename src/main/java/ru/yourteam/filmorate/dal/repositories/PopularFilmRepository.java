package ru.yourteam.filmorate.dal.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dal.mappers.PopularFilmRowMapper;
import ru.yourteam.filmorate.dto.PopularFilmDto;

@Repository
@RequiredArgsConstructor
public class PopularFilmRepository {

    private final JdbcTemplate jdbc;
    private final PopularFilmRowMapper rowMapper;

    // без фильтров
    private static final String FIND_POPULAR_ALL =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, " +
            "       COUNT(DISTINCT l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
            "ORDER BY likes_count DESC, f.film_id ASC " +
            "LIMIT ?";

    // только жанр
    private static final String FIND_POPULAR_BY_GENRE =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, " +
            "       COUNT(DISTINCT l.user_id) AS likes_count " +
            "FROM films f " +
            "JOIN film_genre fg ON fg.film_id = f.film_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE fg.genre_id = ? " +
            "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
            "ORDER BY likes_count DESC, f.film_id ASC " +
            "LIMIT ?";

    // только год
    private static final String FIND_POPULAR_BY_YEAR =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, " +
            "       COUNT(DISTINCT l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
            "ORDER BY likes_count DESC, f.film_id ASC " +
            "LIMIT ?";

    // и жанр, и год
    private static final String FIND_POPULAR_BY_GENRE_AND_YEAR =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, " +
            "       COUNT(DISTINCT l.user_id) AS likes_count " +
            "FROM films f " +
            "JOIN film_genre fg ON fg.film_id = f.film_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE fg.genre_id = ? " +
            "  AND EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id " +
            "ORDER BY likes_count DESC, f.film_id ASC " +
            "LIMIT ?";

    public List<PopularFilmDto> findMostPopular(int count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            // жанр + год
            return jdbc.query(FIND_POPULAR_BY_GENRE_AND_YEAR, rowMapper, genreId, year, count);
        } else if (genreId != null) {
            // только жанр
            return jdbc.query(FIND_POPULAR_BY_GENRE, rowMapper, genreId, count);
        } else if (year != null) {
            // только год
            return jdbc.query(FIND_POPULAR_BY_YEAR, rowMapper, year, count);
        } else {
            // без фильтров
            return jdbc.query(FIND_POPULAR_ALL, rowMapper, count);
        }
    }
}
