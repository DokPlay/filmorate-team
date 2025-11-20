package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private static final String INSERT_LIKE =
        "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
    private static final String DELETE_LIKE =
        "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";

    private final JdbcTemplate jdbcTemplate;

    public void addLike(int filmId, int userId) {
        try {
            jdbcTemplate.update(INSERT_LIKE, filmId, userId);
        } catch (DuplicateKeyException ignored) {
            // Повторное добавление лайка не должно приводить к ошибке
        }
    }

    public int removeLike(int filmId, int userId) {
        return jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }
}
