package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.repository.DirectorRepository;
import ru.yourteam.filmorate.service.DirectorServiceImpl;
import ru.yourteam.filmorate.service.SortMode;

/**
 * Юнит-тесты для DirectorServiceImpl: валидируем пагинацию и делегирование репозиторию.
 */
@ExtendWith(MockitoExtension.class)
class DirectorServiceImplTest {

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DirectorServiceImpl directorService;

    @Test
    void getFilmsByDirector_whenLimitAboveMax_isClampedAndDelegated() {
        long directorId = 7L;
        int requestedLimit = 1_000; // заведомо больше MAX_PAGE_SIZE=500 в сервисе
        int offset = 5;

        Director director = new Director();
        director.setId(directorId);
        director.setName("Кристофер Нолан");

        // мок: режиссер найден
        when(directorRepository.getById(directorId)).thenReturn(Optional.of(director));
        // мок: репозиторий вернет пустой список, нам важен вызов с ограниченным limit
        when(directorRepository.findFilmsByDirectorOrderByLikes(directorId, 500, offset))
                .thenReturn(Collections.emptyList());

        // выполняем
        assertThat(directorService.getFilmsByDirector(directorId, SortMode.LIKES, requestedLimit, offset))
                .isEmpty();

        // проверяем делегирование с обрезанным limit
        verify(directorRepository).getById(directorId);
        verify(directorRepository).findFilmsByDirectorOrderByLikes(directorId, 500, offset);
        verifyNoMoreInteractions(directorRepository);
    }

    @Test
    void getFilmsByDirector_whenLimitIsZero_throwsValidationException() {
        long directorId = 3L;

        when(directorRepository.getById(directorId))
                .thenReturn(Optional.of(new Director()));

        assertThatThrownBy(() -> directorService.getFilmsByDirector(directorId, SortMode.YEAR, 0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("limit");

        // убеждаемся, что бизнес-запрос к репозиторию не случился
        verify(directorRepository).getById(directorId);
        verify(directorRepository, never()).findFilmsByDirectorOrderByYear(directorId, 0, 0);
    }

    @Test
    void getFilmsByDirector_whenOffsetIsNegative_throwsValidationException() {
        long directorId = 4L;

        when(directorRepository.getById(directorId))
                .thenReturn(Optional.of(new Director()));

        assertThatThrownBy(() -> directorService.getFilmsByDirector(directorId, SortMode.LIKES, 10, -1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("offset");

        verify(directorRepository).getById(directorId);
        verify(directorRepository, never()).findFilmsByDirectorOrderByLikes(directorId, 10, -1);
    }

    @Test
    void getFilmsByDirector_whenDirectorMissing_throwsNotFound() {
        long missingDirectorId = 99L;
        when(directorRepository.getById(missingDirectorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.getFilmsByDirector(missingDirectorId, SortMode.YEAR, 10, 0))
                .isInstanceOf(EntityNotFoundException.class);

        verify(directorRepository).getById(missingDirectorId);
        verifyNoMoreInteractions(directorRepository);
    }
}
