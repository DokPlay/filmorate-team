package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yourteam.filmorate.dal.repositories.PopularFilmRepository;
import ru.yourteam.filmorate.dto.PopularFilmDto;
import ru.yourteam.filmorate.exception.ValidationException; // commit: обновлён пакет исключения
import ru.yourteam.filmorate.service.PopularFilmServiceImpl;

/**
 * Юнит-тесты для чистой логики PopularFilmServiceImpl.
 * Здесь мы не трогаем БД:
 * - мокируем PopularFilmRepository;
 * - проверяем только нормализацию count и валидацию фильтров genreId/year.
 */
@ExtendWith(MockitoExtension.class)
class PopularFilmServiceUnitTest {

    @Mock
    private PopularFilmRepository popularFilmRepository;

    @InjectMocks
    private PopularFilmServiceImpl popularFilmService;

    @Test
    void getMostPopular_whenCountIsZero_usesDefaultLimit() {
        int requestedCount = 0;
        Integer genreId = null;
        Integer year = null;

        // сервис должен заменить 0 на дефолтное значение 10
        int expectedCount = 10;

        when(popularFilmRepository.findMostPopular(expectedCount, genreId, year))
                .thenReturn(Collections.emptyList());

        List<PopularFilmDto> result =
                popularFilmService.getMostPopular(requestedCount, genreId, year);

        verify(popularFilmRepository).findMostPopular(expectedCount, genreId, year);
        verifyNoMoreInteractions(popularFilmRepository);
    }

    @Test
    void getMostPopular_whenCountIsNegative_usesDefaultLimit() {
        int requestedCount = -5;
        Integer genreId = null;
        Integer year = null;

        int expectedCount = 10;

        when(popularFilmRepository.findMostPopular(expectedCount, genreId, year))
                .thenReturn(Collections.emptyList());

        List<PopularFilmDto> result =
                popularFilmService.getMostPopular(requestedCount, genreId, year);

        verify(popularFilmRepository).findMostPopular(expectedCount, genreId, year);
        verifyNoMoreInteractions(popularFilmRepository);
    }

    @Test
    void getMostPopular_whenCountIsGreaterThanMax_usesMaxLimit() {
        int requestedCount = 1_000;
        Integer genreId = null;
        Integer year = null;

        int expectedCount = 100; // MAX_POPULAR_LIMIT в сервисе

        when(popularFilmRepository.findMostPopular(expectedCount, genreId, year))
                .thenReturn(Collections.emptyList());

        List<PopularFilmDto> result =
                popularFilmService.getMostPopular(requestedCount, genreId, year);

        verify(popularFilmRepository).findMostPopular(expectedCount, genreId, year);
        verifyNoMoreInteractions(popularFilmRepository);
    }

    @Test
    void getMostPopular_whenParamsAreValid_passesFiltersAsIs() {
        int requestedCount = 5;
        Integer genreId = 3;
        Integer year = 2005;

        int expectedCount = requestedCount;

        when(popularFilmRepository.findMostPopular(expectedCount, genreId, year))
                .thenReturn(Collections.emptyList());

        List<PopularFilmDto> result =
                popularFilmService.getMostPopular(requestedCount, genreId, year);

        verify(popularFilmRepository).findMostPopular(expectedCount, genreId, year);
        verifyNoMoreInteractions(popularFilmRepository);
    }

    @Test
    void getMostPopular_whenYearIsLessThanMin_throwsValidationExceptionAndDoesNotCallRepository() {
        int requestedCount = 10;
        Integer genreId = null;
        Integer year = 1800; // меньше MIN_FILM_YEAR = 1895

        assertThatThrownBy(() -> popularFilmService.getMostPopular(requestedCount, genreId, year))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Год релиза не может быть меньше");

        verifyNoInteractions(popularFilmRepository);
    }

    @Test
    void getMostPopular_whenGenreIdIsNotPositive_throwsValidationExceptionAndDoesNotCallRepository() {
        int requestedCount = 10;
        Integer genreId = 0;
        Integer year = null;

        assertThatThrownBy(() -> popularFilmService.getMostPopular(requestedCount, genreId, year))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Идентификатор жанра должен быть положительным");

        verifyNoInteractions(popularFilmRepository);
    }
}
