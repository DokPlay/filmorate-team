package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yourteam.filmorate.dal.repositories.searchRepository.SearchRepository;
import ru.yourteam.filmorate.dto.searchDto.FilmSearchDto;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.service.SearchService;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    void getAllSortedByRatingFilms_whenByIsNull_defaultsToDirectorSearch() {
        String query = "Nolan";

        Film film = new Film();
        film.setId(1);
        film.setName("Inception");
        film.setDescription("Dreams inside dreams");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        // Проверяем, что сервис сам подставляет поиск по режиссеру при пустом параметре "by".
        when(searchRepository.getAllSortedByRatingFilms(SearchService.Type.DIRECTOR, query))
                .thenReturn(List.of(film));

        List<FilmSearchDto> result = searchService.getAllSortedByRatingFilms(query, null);

        assertThat(result)
                .as("Должна вернуться одна запись с фильма, найденного через дефолтный поиск по режиссеру")
                .hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Inception");

        verify(searchRepository).getAllSortedByRatingFilms(SearchService.Type.DIRECTOR, query);
        verifyNoMoreInteractions(searchRepository);
    }
}
