package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

/**
 * Интеграционные тесты контроллера рекомендаций:
 * GET /users/{id}/recommendations
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecommendationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int TEST_MPA_ID = 20;

    private static final int USER_TARGET = 3000;
    private static final int USER_SIMILAR = 3001;
    private static final int USER_LESS_SIMILAR = 3002;
    private static final int USER_WITHOUT_LIKES = 3003;

    private static final int FILM_COMMON_1 = 2000;
    private static final int FILM_COMMON_2 = 2001;
    private static final int FILM_RECOMMENDED = 2002;
    private static final int FILM_LESS_SIMILAR_ONLY = 2003;

    @BeforeEach
    void cleanTestData() {
        // Чистим только тестовые данные (чтобы не трогать то, что создаётся миграциями)
        jdbcTemplate.update(
                "DELETE FROM likes WHERE user_id >= ? OR film_id >= ?",
                USER_TARGET,
                FILM_COMMON_1
        );
        jdbcTemplate.update(
                "DELETE FROM films WHERE film_id >= ?",
                FILM_COMMON_1
        );
        jdbcTemplate.update(
                "DELETE FROM users WHERE user_id >= ?",
                USER_TARGET
        );
        jdbcTemplate.update(
                "DELETE FROM mpa WHERE mpa_id = ?",
                TEST_MPA_ID
        );

        jdbcTemplate.update(
                "INSERT INTO mpa (mpa_id, mpa_name) VALUES (?, ?)",
                TEST_MPA_ID,
                "TEST_MPA_FOR_CONTROLLER"
        );
    }

    @Test
    void getRecommendations_shouldReturnJsonArrayWithRecommendedFilms() throws Exception {
        // given
        insertUser(USER_TARGET, "target-controller@test.ru", "target-controller");
        insertUser(USER_SIMILAR, "similar-controller@test.ru", "similar-controller");
        insertUser(USER_LESS_SIMILAR, "less-controller@test.ru", "less-controller");

        insertFilm(FILM_COMMON_1, "Common film 1");
        insertFilm(FILM_COMMON_2, "Common film 2");
        insertFilm(FILM_RECOMMENDED, "Recommended film");
        insertFilm(FILM_LESS_SIMILAR_ONLY, "Less similar only film");

        // целевой пользователь лайкает два фильма
        insertLike(USER_TARGET, FILM_COMMON_1);
        insertLike(USER_TARGET, FILM_COMMON_2);

        // самый похожий пользователь: два общих + один новый
        insertLike(USER_SIMILAR, FILM_COMMON_1);
        insertLike(USER_SIMILAR, FILM_COMMON_2);
        insertLike(USER_SIMILAR, FILM_RECOMMENDED);

        // менее похожий: пересечение по одному фильму
        insertLike(USER_LESS_SIMILAR, FILM_COMMON_1);
        insertLike(USER_LESS_SIMILAR, FILM_LESS_SIMILAR_ONLY);

        // when / then
        mockMvc.perform(get("/users/{id}/recommendations", USER_TARGET))
                .andExpect(status().isOk())
                // ожидаем ровно один элемент
                .andExpect(jsonPath("$", hasSize(1)))
                // filmId того самого фильма, который лайкнул самый похожий пользователь
                .andExpect(jsonPath("$[0].filmId").value(FILM_RECOMMENDED))
                // relevanceScore = 1 (один похожий пользователь лайкнул этот фильм)
                .andExpect(jsonPath("$[0].relevanceScore").value(1))
                // проверим, что поля name/description/… вообще есть
                .andExpect(jsonPath("$[0].name").value("Recommended film"));
    }

    @Test
    void getRecommendations_whenUserHasNoLikes_returnsEmptyArray() throws Exception {
        // given
        insertUser(USER_WITHOUT_LIKES, "nolikes-controller@test.ru", "nolikes-controller");

        // when / then
        mockMvc.perform(get("/users/{id}/recommendations", USER_WITHOUT_LIKES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getRecommendations_whenUserDoesNotExist_returns404() throws Exception {
        int notExistingUserId = 999998;

        mockMvc.perform(get("/users/{id}/recommendations", notExistingUserId))
                .andExpect(status().isNotFound());
    }

    // ----------------- Вспомогательные методы -----------------

    private void insertUser(int userId, String email, String login) {
        int updated = jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, user_name, birthday) " +
                        "VALUES (?, ?, ?, ?, ?)",
                userId,
                email,
                login,
                login + "_name",
                LocalDate.of(1990, 1, 1)
        );
        assertThat(updated).isEqualTo(1);
    }

    private void insertFilm(int filmId, String name) {
        int updated = jdbcTemplate.update(
                "INSERT INTO films (film_id, film_name, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                filmId,
                name,
                "Test description for controller",
                LocalDate.of(2001, 1, 1),
                110,
                TEST_MPA_ID
        );
        assertThat(updated).isEqualTo(1);
    }

    private void insertLike(int userId, int filmId) {
        int updated = jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId,
                userId
        );
        assertThat(updated).isEqualTo(1);
    }
}
