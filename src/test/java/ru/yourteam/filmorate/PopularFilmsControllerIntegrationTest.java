package ru.yourteam.filmorate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Интеграционный тест для PopularFilmsController.
 * Проверяем:
 * - базовый сценарий без параметров;
 * - фильтрацию по жанру и году;
 * - валидацию параметров count и year.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PopularFilmsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Отдельный тестовый рейтинг, чтобы не трогать данные миграций.
    private static final int TEST_MPA_ID = 30;

    private static final int USER_1 = 4000;
    private static final int USER_2 = 4001;
    private static final int USER_3 = 4002;

    private static final int FILM_1 = 3000;
    private static final int FILM_2 = 3001;
    private static final int FILM_3 = 3002;
    private static final int FILM_4 = 3003;

    private static final int GENRE_COMEDY = 1;
    private static final int GENRE_DRAMA = 2;

    @BeforeEach
    void cleanTestData() {
        // Чистим все тестовые данные, которые могут остаться от других интеграционных тестов.
        // Используем диапазоны id, не пересекающиеся с миграционными данными.
        jdbcTemplate.update(
                "DELETE FROM likes WHERE user_id >= ? OR film_id >= ?",
                2000,
                1000
        );
        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id >= ?",
                1000
        );
        jdbcTemplate.update(
                "DELETE FROM films WHERE film_id >= ?",
                1000
        );
        jdbcTemplate.update(
                "DELETE FROM users WHERE user_id >= ?",
                2000
        );
        jdbcTemplate.update(
                "DELETE FROM mpa WHERE mpa_id >= ?",
                TEST_MPA_ID
        );
        jdbcTemplate.update(
                "INSERT INTO mpa (mpa_id, mpa_name) VALUES (?, ?)",
                TEST_MPA_ID,
                "TEST_MPA_POPULAR"
        );
    }

    /**
     * Базовый набор:
     *
     * FILM_1: Комедия, 1999, 3 лайка
     * FILM_2: Комедия, 1999, 1 лайк
     * FILM_3: Драма,   2000, 2 лайка
     * FILM_4: Комедия, 2000, 0 лайков
     */
    private void insertBaseData() {
        insertUser(USER_1, "u1-popular@test.ru", "u1-popular");
        insertUser(USER_2, "u2-popular@test.ru", "u2-popular");
        insertUser(USER_3, "u3-popular@test.ru", "u3-popular");

        insertFilm(FILM_1, "Film 1", 1999);
        insertFilm(FILM_2, "Film 2", 1999);
        insertFilm(FILM_3, "Film 3", 2000);
        insertFilm(FILM_4, "Film 4", 2000);

        insertFilmGenre(FILM_1, GENRE_COMEDY);
        insertFilmGenre(FILM_2, GENRE_COMEDY);
        insertFilmGenre(FILM_3, GENRE_DRAMA);
        insertFilmGenre(FILM_4, GENRE_COMEDY);

        // FILM_1: 3 лайка
        insertLike(USER_1, FILM_1);
        insertLike(USER_2, FILM_1);
        insertLike(USER_3, FILM_1);

        // FILM_2: 1 лайк
        insertLike(USER_1, FILM_2);

        // FILM_3: 2 лайка
        insertLike(USER_2, FILM_3);
        insertLike(USER_3, FILM_3);

        // FILM_4: 0 лайков — ничего не добавляем
    }

    private void insertUser(int userId, String email, String login) {
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

    private void insertFilm(int filmId, String name, int year) {
        jdbcTemplate.update(
                "INSERT INTO films (film_id, film_name, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                filmId,
                name,
                "Popular films controller test",
                LocalDate.of(year, 1, 1),
                100,
                TEST_MPA_ID
        );
    }

    private void insertFilmGenre(int filmId, int genreId) {
        jdbcTemplate.update(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                filmId,
                genreId
        );
    }

    private void insertLike(int userId, int filmId) {
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId,
                userId
        );
    }

    @Test
    void getPopularFilms_withoutParams_returnsOrderedList() throws Exception {
        // given
        insertBaseData();

        // when / then
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                // порядок по убыванию лайков, при равенстве — по film_id
                .andExpect(jsonPath("$[0].filmId").value(FILM_1)) // 3 лайка
                .andExpect(jsonPath("$[1].filmId").value(FILM_3)) // 2 лайка
                .andExpect(jsonPath("$[2].filmId").value(FILM_2)) // 1 лайк
                .andExpect(jsonPath("$[3].filmId").value(FILM_4)); // 0 лайков
    }

    @Test
    void getPopularFilms_withGenreAndYear_returnsFilteredAndOrderedList() throws Exception {
        // given
        insertBaseData();

        // when / then
        mockMvc.perform(
                        get("/films/popular")
                                .param("count", "2")
                                .param("genreId", String.valueOf(GENRE_COMEDY))
                                .param("year", "1999")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].filmId").value(FILM_1))
                .andExpect(jsonPath("$[1].filmId").value(FILM_2));
    }

    @Test
    void getPopularFilms_whenCountIsZero_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        get("/films/popular")
                                .param("count", "0")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPopularFilms_whenYearIsTooSmall_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        get("/films/popular")
                                .param("year", "1800")
                )
                .andExpect(status().isBadRequest());
    }
}
