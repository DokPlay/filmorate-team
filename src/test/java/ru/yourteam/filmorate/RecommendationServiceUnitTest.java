package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository;// PR от Дмитрия
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository.UserLikeRow;// PR от Дмитрия
import ru.yourteam.filmorate.dto.RecommendationDto;
import ru.yourteam.filmorate.exception.NotFoundException; // commit: обновлён пакет исключения
import ru.yourteam.filmorate.model.Film;// PR от Дмитрия
import ru.yourteam.filmorate.service.RecommendationService;

/**
 * Юнит-тесты для чистой логики RecommendationService.
 * Здесь мы не трогаем БД:
 * - мокируем RecommendationRepository;
 * - проверяем только алгоритм подбора фильмов.
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceUnitTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendationsForUser_whenUserDoesNotExist_throwsNotFound() {
        int userId = 1;
        when(recommendationRepository.userExists(userId)).thenReturn(false);

        assertThatThrownBy(() -> recommendationService.getRecommendationsForUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=" + userId);

        verify(recommendationRepository).userExists(userId);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    void getRecommendationsForUser_whenUserHasNoLikes_returnsEmptyListEvenIfOthersHaveLikes() {
        int targetUserId = 10;

        when(recommendationRepository.userExists(targetUserId)).thenReturn(true);

        // Лайки есть только у других пользователей.
        List<UserLikeRow> likes = List.of(
                new UserLikeRow(targetUserId + 1, 100),
                new UserLikeRow(targetUserId + 2, 100),
                new UserLikeRow(targetUserId + 2, 101)
        );
        when(recommendationRepository.findAllLikes()).thenReturn(likes);

        List<RecommendationDto> result =
                recommendationService.getRecommendationsForUser(targetUserId);

        assertThat(result).isEmpty();

        verify(recommendationRepository).userExists(targetUserId);
        verify(recommendationRepository).findAllLikes();
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    void getRecommendationsForUser_returnsFilmsLikedByMostSimilarUsersOnly() {
        int targetUserId = 42;
        int similarUserId = 100;
        int lessSimilarUserId = 101;

        int filmCommon1 = 1000;
        int filmCommon2 = 1001;
        int filmRecommended = 1002;
        int filmLessSimilarOnly = 1003;

        when(recommendationRepository.userExists(targetUserId)).thenReturn(true);

        List<UserLikeRow> likes = List.of(
                // Целевой пользователь
                new UserLikeRow(targetUserId, filmCommon1),
                new UserLikeRow(targetUserId, filmCommon2),

                // Сильно похожий: 2 общих лайка + 1 уникальный
                new UserLikeRow(similarUserId, filmCommon1),
                new UserLikeRow(similarUserId, filmCommon2),
                new UserLikeRow(similarUserId, filmRecommended),

                // Менее похожий: 1 общий лайк + свой фильм
                new UserLikeRow(lessSimilarUserId, filmCommon1),
                new UserLikeRow(lessSimilarUserId, filmLessSimilarOnly)
        );
        when(recommendationRepository.findAllLikes()).thenReturn(likes);

        when(recommendationRepository.findFilmsByIds(anyCollection()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Collection<Integer> ids = invocation.getArgument(0, Collection.class);
                    return ids.stream()
                            .map(id -> createFilm(id, id == filmRecommended ? "Recommended" : "Film " + id))
                            .collect(Collectors.toList());
                });

        List<RecommendationDto> result =
                recommendationService.getRecommendationsForUser(targetUserId);

        assertThat(result)
                .as("Должен быть один фильм из максимально похожего пользователя")
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getFilmId()).isEqualTo(filmRecommended);
                    assertThat(dto.getRelevanceScore()).isEqualTo(1);
                    assertThat(dto.getName()).isEqualTo("Recommended");
                });
    }

    @Test
    void getRecommendationsForUser_whenSeveralSimilarUsersLikeSameFilm_scoresAccumulate() {
        int targetUserId = 7;
        int similarUser1 = 11;
        int similarUser2 = 12;

        int commonFilm1 = 2000;
        int commonFilm2 = 2001;
        int recommendedFilm = 2002;

        when(recommendationRepository.userExists(targetUserId)).thenReturn(true);

        List<UserLikeRow> likes = List.of(
                // Целевой
                new UserLikeRow(targetUserId, commonFilm1),
                new UserLikeRow(targetUserId, commonFilm2),

                // Похожий 1
                new UserLikeRow(similarUser1, commonFilm1),
                new UserLikeRow(similarUser1, commonFilm2),
                new UserLikeRow(similarUser1, recommendedFilm),

                // Похожий 2
                new UserLikeRow(similarUser2, commonFilm1),
                new UserLikeRow(similarUser2, commonFilm2),
                new UserLikeRow(similarUser2, recommendedFilm)
        );

        when(recommendationRepository.findAllLikes()).thenReturn(likes);
        when(recommendationRepository.findFilmsByIds(anyCollection()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Collection<Integer> ids = invocation.getArgument(0, Collection.class);
                    return ids.stream()
                            .map(id -> createFilm(id, "Film " + id))
                            .collect(Collectors.toList());
                });

        List<RecommendationDto> result =
                recommendationService.getRecommendationsForUser(targetUserId);

        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getFilmId()).isEqualTo(recommendedFilm);
                    assertThat(dto.getRelevanceScore())
                            .as("Фильм лайкнули два максимально похожих пользователя")
                            .isEqualTo(2);
                });
    }

    private Film createFilm(int filmId, String name) {
        Film film = new Film();
        film.setId(filmId);
        film.setName(name);
        film.setDescription("test description");
        film.setDuration(100);
        film.setMpaId(1);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        return film;
    }
}
