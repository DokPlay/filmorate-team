package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yourteam.filmorate.dal.repositories.PopularFilmRepository;
import ru.yourteam.filmorate.dto.PopularFilmDto;

/**
 * Интеграционные тесты PopularFilmRepository на H2 + реальные миграции Flyway.
 *
 * Наследуемся от AbstractIntegrationTest, чтобы:
 * - использовать общий JdbcTemplate;
 * - пользоваться helper-методами insertTestUser / insertTestFilm / insertTestLike;
 * - сидеть в диапазоне id (TEST_*_MIN_ID), который базовый класс безопасно чистит в @BeforeEach.
 */
public class PopularFilmRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final int USER_1 = TEST_USER_MIN_ID;
    private static final int USER_2 = TEST_USER_MIN_ID + 1;
    private static final int USER_3 = TEST_USER_MIN_ID + 2;

    private static final int FILM_1 = TEST_FILM_MIN_ID;
    private static final int FILM_2 = TEST_FILM_MIN_ID + 1;
    private static final int FILM_3 = TEST_FILM_MIN_ID + 2;
    private static final int FILM_4 = TEST_FILM_MIN_ID + 3;

    private static final int GENRE_COMEDY = 1; // Комедия из V4__init_data.sql
    private static final int GENRE_DRAMA = 2;  // Драма из V4__init_data.sql

    @Autowired
    private PopularFilmRepository popularFilmRepository;

    /**
     * Базовый набор данных:
     *
     * 3 пользователя (USER_1..USER_3)
     * 4 фильма (FILM_1..FILM_4)
     *
     * FILM_1: Комедия, 1999 год, 3 лайка
     * FILM_2: Комедия, 1999 год, 1 лайк
     * FILM_3: Драма,   2000 год, 2 лайка
     * FILM_4: Комедия, 2000 год, 0 лайков
     */
    private void insertBaseData() {
        // пользователи
        insertTestUser(USER_1, "user1@test.ru", "user1");
        insertTestUser(USER_2, "user2@test.ru", "user2");
        insertTestUser(USER_3, "user3@test.ru", "user3");

        // фильмы
        insertTestFilm(FILM_1, "Film 1");
        insertTestFilm(FILM_2, "Film 2");
        insertTestFilm(FILM_3, "Film 3");
        insertTestFilm(FILM_4, "Film 4");

        // привязка жанров
        insertFilmGenre(FILM_1, GENRE_COMEDY);
        insertFilmGenre(FILM_2, GENRE_COMEDY);
        insertFilmGenre(FILM_3, GENRE_DRAMA);
        insertFilmGenre(FILM_4, GENRE_COMEDY);

        // лайки
        // FILM_1: 3 лайка
        insertTestLike(USER_1, FILM_1);
        insertTestLike(USER_2, FILM_1);
        insertTestLike(USER_3, FILM_1);

        // FILM_2: 1 лайк
        insertTestLike(USER_1, FILM_2);

        // FILM_3: 2 лайка
        insertTestLike(USER_2, FILM_3);
        insertTestLike(USER_3, FILM_3);

        // FILM_4: 0 лайков — ничего не добавляем
    }

    private void insertFilmGenre(int filmId, int genreId) {
        int updated = jdbcTemplate.update(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                filmId,
                genreId
        );
        assertThat(updated)
                .as("Должна вставиться ровно одна строка в film_genre")
                .isEqualTo(1);
    }

    @Test
    void findMostPopular_withoutFilters_returnsAllFilmsOrderedByLikesDescThenId() {
        // given
        insertBaseData();

        // when
        List<PopularFilmDto> result =
                popularFilmRepository.findMostPopular(10, null, null);

        // then
        assertThat(result)
                .extracting(PopularFilmDto::getFilmId)
                .containsExactly(FILM_1, FILM_3, FILM_2, FILM_4);

        assertThat(result.get(0).getLikesCount())
                .as("У самого популярного фильма должно быть 3 лайка")
                .isEqualTo(3);
        assertThat(result.get(1).getLikesCount()).isEqualTo(2);
        assertThat(result.get(2).getLikesCount()).isEqualTo(1);
        assertThat(result.get(3).getLikesCount()).isEqualTo(0);
    }

    @Test
    void findMostPopular_withGenreFilter_returnsOnlyFilmsOfThisGenre() {
        // given
        insertBaseData();

        // when
        List<PopularFilmDto> result =
                popularFilmRepository.findMostPopular(10, GENRE_COMEDY, null);

        // then
        assertThat(result)
                .extracting(PopularFilmDto::getFilmId)
                .containsExactly(FILM_1, FILM_2, FILM_4);
    }

    @Test
    void findMostPopular_withYearFilter_returnsOnlyFilmsOfThisYear() {
        // given
        insertBaseData();

        // when
        List<PopularFilmDto> result =
                popularFilmRepository.findMostPopular(10, null, 1999);

        // then
        assertThat(result)
                .extracting(PopularFilmDto::getFilmId)
                .containsExactly(FILM_1, FILM_2);
    }

    @Test
    void findMostPopular_withGenreAndYearFilter_returnsIntersection() {
        // given
        insertBaseData();

        // when
        List<PopularFilmDto> result =
                popularFilmRepository.findMostPopular(10, GENRE_COMEDY, 1999);

        // then
        assertThat(result)
                .extracting(PopularFilmDto::getFilmId)
                .containsExactly(FILM_1, FILM_2);
    }
}
