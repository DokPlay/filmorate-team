package ru.yourteam.filmorate.dal.repositories;//это пакет от дмитрия в PR смотреть.
//Таблица likes и films по именам колонок опираются на существующие мапперы FilmRowMapper и SQL из веток add-common-films / add-reviews.

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.repository.jpa.FilmJpaRepository;

@Repository
@RequiredArgsConstructor
public class RecommendationRepository {

    private final JdbcTemplate jdbc;

    // JPA-репозиторий с JOIN FETCH, чтобы не загружать жанры и режиссёров по одному (N+1)
    private final FilmJpaRepository filmJpaRepository;

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

        // Подтягиваем связанные сущности одной порцией, чтобы не получить лавину дополнительных запросов
        // при сериализации фильмов в DTO (жанры, режиссёры, MPA).
        return filmJpaRepository.findByIdInWithRelations(filmIds);
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
