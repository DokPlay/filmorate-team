package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository;
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository.UserLikeRow;
import ru.yourteam.filmorate.model.Film;

/**
 * Интеграционный тест RecommendationRepository на реальной БД H2 + миграциях Flyway.
 *
 * Тут проверяем чистую работу SQL-слоя без сервисной логики:
 * - userExists возвращает корректный флаг;
 * - findAllLikes отдаёт полные данные из таблицы likes;
 * - findFilmsByIds мапит базовые поля фильма.
 */
public class RecommendationRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final int EXISTING_USER = TEST_USER_MIN_ID;
    private static final int MISSING_USER = TEST_USER_MIN_ID + 99;

    private static final int FILM_FIRST = TEST_FILM_MIN_ID;
    private static final int FILM_SECOND = TEST_FILM_MIN_ID + 1;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Test
    void userExists_returnsTrueOnlyForPersistedUser() {
        // Создаём только одного пользователя с минимальным id, чтобы тест был максимально предсказуем
        insertTestUser(EXISTING_USER, "repository-user@test.ru", "repo-user");

        boolean exists = recommendationRepository.userExists(EXISTING_USER);
        boolean missing = recommendationRepository.userExists(MISSING_USER);

        assertThat(exists)
                .as("Пользователь с id %s был записан в таблицу users".formatted(EXISTING_USER))
                .isTrue();
        assertThat(missing)
                .as("Несуществующий id не должен отмечаться как присутствующий")
                .isFalse();
    }

    @Test
    void findAllLikes_returnsCompleteLikeRowsWithUserAndFilmIds() {
        // Готовим минимальные сущности, чтобы проверить корректность выборки likes
        insertTestUser(EXISTING_USER, "likes-user@test.ru", "likes-user");
        insertTestUser(EXISTING_USER + 1, "second-user@test.ru", "second-user");

        insertTestFilm(FILM_FIRST, "Repository Film 1");
        insertTestFilm(FILM_SECOND, "Repository Film 2");

        insertTestLike(EXISTING_USER, FILM_FIRST);
        insertTestLike(EXISTING_USER + 1, FILM_SECOND);

        List<UserLikeRow> rows = recommendationRepository.findAllLikes();

        assertThat(rows)
                .as("Ожидаем два лайка с сохранением userId/filmId")
                .containsExactlyInAnyOrder(
                        new UserLikeRow(EXISTING_USER, FILM_FIRST),
                        new UserLikeRow(EXISTING_USER + 1, FILM_SECOND)
                );
    }

    @Test
    void findFilmsByIds_mapsBasicFilmAttributes() {
        insertTestFilm(FILM_FIRST, "Film by id", LocalDate.of(2020, 5, 5));

        List<Film> films = recommendationRepository.findFilmsByIds(List.of(FILM_FIRST));

        assertThat(films)
                .as("Должен вернуться ровно один фильм по запрошенному id")
                .hasSize(1);

        Film film = films.getFirst();
        assertThat(film.getId()).isEqualTo(FILM_FIRST);
        assertThat(film.getName()).isEqualTo("Film by id");
        assertThat(film.getReleaseDate())
                .as("Дата выхода считывается из таблицы films")
                .isEqualTo(LocalDate.of(2020, 5, 5));
    }
}
