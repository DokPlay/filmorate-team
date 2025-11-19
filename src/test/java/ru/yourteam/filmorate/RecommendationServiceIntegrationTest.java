package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yourteam.filmorate.dto.RecommendationDto;
import ru.yourteam.filmorate.exceptions.NotFoundException; // PR от Дмитрия
import ru.yourteam.filmorate.service.RecommendationService;

/**
 * Интеграционные тесты RecommendationService на H2 +
 * реальные миграции Flyway.
 */
public class RecommendationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RecommendationService recommendationService;

    // Идентификаторы, которые используем только в тестах (сидим в диапазоне из базового класса)
    private static final int USER_TARGET = TEST_USER_MIN_ID;
    private static final int USER_SIMILAR = TEST_USER_MIN_ID + 1;
    private static final int USER_LESS_SIMILAR = TEST_USER_MIN_ID + 2;
    private static final int USER_WITHOUT_LIKES = TEST_USER_MIN_ID + 3;

    private static final int FILM_COMMON_1 = TEST_FILM_MIN_ID;
    private static final int FILM_COMMON_2 = TEST_FILM_MIN_ID + 1;
    private static final int FILM_RECOMMENDED = TEST_FILM_MIN_ID + 2;
    private static final int FILM_LESS_SIMILAR_ONLY = TEST_FILM_MIN_ID + 3;

    @Test
    void getRecommendationsForUser_shouldReturnFilmsFromMostSimilarUsers() {
        // given
        insertTestUser(USER_TARGET, "target@test.ru", "target");
        insertTestUser(USER_SIMILAR, "similar@test.ru", "similar");
        insertTestUser(USER_LESS_SIMILAR, "less@test.ru", "less");

        insertTestFilm(FILM_COMMON_1, "Common film 1");
        insertTestFilm(FILM_COMMON_2, "Common film 2");
        insertTestFilm(FILM_RECOMMENDED, "Recommended film");
        insertTestFilm(FILM_LESS_SIMILAR_ONLY, "Less similar only film");

        // Пользователь-цель лайкает два фильма
        insertTestLike(USER_TARGET, FILM_COMMON_1);
        insertTestLike(USER_TARGET, FILM_COMMON_2);

        // Самый похожий: лайкает те же два + один уникальный
        insertTestLike(USER_SIMILAR, FILM_COMMON_1);
        insertTestLike(USER_SIMILAR, FILM_COMMON_2);
        insertTestLike(USER_SIMILAR, FILM_RECOMMENDED);

        // Менее похожий: пересечение только по одному фильму
        insertTestLike(USER_LESS_SIMILAR, FILM_COMMON_1);
        insertTestLike(USER_LESS_SIMILAR, FILM_LESS_SIMILAR_ONLY);

        // when
        List<RecommendationDto> recommendations =
                recommendationService.getRecommendationsForUser(USER_TARGET);

        // then
        assertThat(recommendations)
                .as("Должен быть ровно один рекомендованный фильм")
                .hasSize(1);

        RecommendationDto dto = recommendations.getFirst();

        assertThat(dto.getFilmId())
                .as("Рекомендуется фильм, который лайкнул самый похожий пользователь, " +
                        "но ещё не лайкнул целевой")
                .isEqualTo(FILM_RECOMMENDED);

        assertThat(dto.getRelevanceScore())
                .as("Вес рекомендации равен количеству похожих пользователей, " +
                        "поставивших лайк этому фильму")
                .isEqualTo(1);
    }

    @Test
    void getRecommendationsForUser_whenUserHasNoLikes_returnsEmptyList() {
        // given
        insertTestUser(USER_WITHOUT_LIKES, "nolikes@test.ru", "nolikes");

        // when
        List<RecommendationDto> recommendations =
                recommendationService.getRecommendationsForUser(USER_WITHOUT_LIKES);

        // then
        assertThat(recommendations)
                .as("Если у пользователя нет лайков, рекомендации пустые")
                .isEmpty();
    }

    @Test
    void getRecommendationsForUser_whenUserDoesNotExist_throwsNotFound() {
        int notExistingUserId = 999999;

        assertThatThrownBy(() -> recommendationService.getRecommendationsForUser(notExistingUserId))
                .as("Для несуществующего пользователя должна лететь NotFoundException")
                .isInstanceOf(NotFoundException.class);
    }
}
