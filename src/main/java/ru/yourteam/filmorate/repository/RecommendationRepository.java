package ru.yourteam.filmorate.dal.repositories;//это пакет от дмитрия в PR смотреть.
//Таблица likes и films по именам колонок опираются на существующие мапперы FilmRowMapper и SQL из веток add-common-films / add-reviews.

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dal.mappers.FilmRowMapper;
import ru.yourteam.filmorate.model.Film;

@Repository
@RequiredArgsConstructor
public class RecommendationRepository {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    /**
     * Проверка, существует ли пользователь в таблице users.
     */
    public boolean userExists(int userId) {
        String sql = "SELECT COUNT(1) FROM users WHERE user_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    /**
     * Возвращает все пары (user_id, film_id) из таблицы likes.
     * Используется для построения матрицы лайков в сервисе рекомендаций.
     */
    public List<UserLikeRow> findAllLikes() {
        String sql = "SELECT user_id, film_id FROM likes";
        return jdbc.query(sql, new UserLikeRowMapper());
    }

    /**
     * Возвращает список фильмов по их идентификаторам.
     */
    public List<Film> findFilmsByIds(Collection<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        // строим IN (?, ?, ?, ...)
        StringBuilder inClause = new StringBuilder();
        int size = filmIds.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                inClause.append(", ");
            }
            inClause.append("?");
        }

        String sql = "SELECT film_id, film_name, description, release_date, duration, mpa_id "
            + "FROM films "
            + "WHERE film_id IN (" + inClause + ")";

        Object[] params = filmIds.toArray();
        return jdbc.query(sql, filmRowMapper, params);
    }

    /**
     * Простая модель строки из таблицы likes: пара (user_id, film_id).
     */
    public static final class UserLikeRow {
        private final int userId;
        private final int filmId;

        public UserLikeRow(int userId, int filmId) {
            this.userId = userId;
            this.filmId = filmId;
        }

        public int getUserId() {
            return userId;
        }

        public int getFilmId() {
            return filmId;
        }
    }

    /**
     * RowMapper для UserLikeRow.
     */
    private static final class UserLikeRowMapper implements RowMapper<UserLikeRow> {

        @Override
        public UserLikeRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            int userId = rs.getInt("user_id");
            int filmId = rs.getInt("film_id");
            return new UserLikeRow(userId, filmId);
        }
    }
}
