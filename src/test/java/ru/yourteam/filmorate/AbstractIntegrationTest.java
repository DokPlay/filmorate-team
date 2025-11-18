package ru.yourteam.filmorate;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый класс для интеграционных тестов:
 * - поднимает Spring Boot контекст;
 * - включает профиль test;
 * - даёт общий JdbcTemplate и методы для подготовки тестовых данных.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    /**
     * Все тестовые пользователи должны иметь id >= TEST_USER_MIN_ID,
     * чтобы их можно было безопасно удалить.
     */
    protected static final int TEST_USER_MIN_ID = 2000;

    /**
     * Все тестовые фильмы должны иметь id >= TEST_FILM_MIN_ID.
     */
    protected static final int TEST_FILM_MIN_ID = 1000;

    /**
     * Отдельный тестовый рейтинг MPA, чтобы не трогать данные из V4__init_data.sql.
     */
    protected static final int TEST_MPA_ID = 999;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepareDatabase() {
        // Удаляем только тестовые лайки
        jdbcTemplate.update(
            "DELETE FROM likes WHERE user_id >= ? OR film_id >= ?",
            TEST_USER_MIN_ID,
            TEST_FILM_MIN_ID
        );

        // Удаляем только тестовые фильмы
        jdbcTemplate.update(
            "DELETE FROM films WHERE film_id >= ?",
            TEST_FILM_MIN_ID
        );

        // Удаляем только тестовых пользователей
        jdbcTemplate.update(
            "DELETE FROM users WHERE user_id >= ?",
            TEST_USER_MIN_ID
        );

        // Чистим и создаём тестовый рейтинг MPA
        jdbcTemplate.update(
            "DELETE FROM mpa WHERE mpa_id = ?",
            TEST_MPA_ID
        );
        jdbcTemplate.update(
            "INSERT INTO mpa (mpa_id, mpa_name) VALUES (?, ?)",
            TEST_MPA_ID,
            "TEST_MPA"
        );
    }

    protected void insertTestUser(int userId, String email, String login) {
        jdbcTemplate.update(
            "INSERT INTO users (user_id, email, login, user_name, birthday) " +
                "VALUES (?, ?, ?, ?, ?)",
            userId,
            email,
            login,
            login + "_name",
            LocalDate.of(1990, 1, 1)
        );
    }

    protected void insertTestFilm(int filmId, String name) {
        jdbcTemplate.update(
            "INSERT INTO films (film_id, film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            filmId,
            name,
            "Test description",
            LocalDate.of(2000, 1, 1),
            100,
            TEST_MPA_ID
        );
    }

    protected void insertTestLike(int userId, int filmId) {
        jdbcTemplate.update(
            "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
            filmId,
            userId
        );
    }
}
