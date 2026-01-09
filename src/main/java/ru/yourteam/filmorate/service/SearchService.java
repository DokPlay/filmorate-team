package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yourteam.filmorate.dal.repositories.searchRepository.SearchRepository;
import ru.yourteam.filmorate.dto.searchDto.FilmSearchDto;
import ru.yourteam.filmorate.mappers.searchMapper.FilmSearchMapper;

import java.util.List;
import java.util.stream.Collectors;

// Сервисный слой для обработки запросов поиска фильмов.
@RequiredArgsConstructor
@Service
public class SearchService {

    private final SearchRepository searchRepository;

    public List<FilmSearchDto> getAllSortedByRatingFilms(String query, String by) {
        // Если клиент не передал параметр "by" или он пустой, то подставляем дефолтный режим поиска по режиссеру,
        // чтобы не ловить NPE и отдавать ожидаемое поведение даже при неполных запросах.
        String normalizedBy = normalizeByParam(by);

        if (query != null) {
            return switch (normalizedBy.toLowerCase()) {
                case "director" -> searchRepository.getAllSortedByRatingFilms(Type.DIRECTOR, query).stream()
                        .map(FilmSearchMapper::mapToFilmSearchDto)
                        .collect(Collectors.toList());
                case "title" -> searchRepository.getAllSortedByRatingFilms(Type.TITLE, query).stream()
                        .map(FilmSearchMapper::mapToFilmSearchDto)
                        .collect(Collectors.toList());
                case "director,title" -> searchRepository.getAllSortedByRatingFilms(Type.ALL, query).stream()
                        .map(FilmSearchMapper::mapToFilmSearchDto)
                        .collect(Collectors.toList());
                default -> throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Неизвестный параметр запроса: " + normalizedBy
                );
            };
        }

        return searchRepository.getAllSortedByRatingFilms(Type.NOTHING, null).stream()
                .map(FilmSearchMapper::mapToFilmSearchDto)
                .collect(Collectors.toList());

    }

    private String normalizeByParam(String by) {
        // Конвертируем значение "by" в безопасный вид: если оно пустое или не передано — ищем по режиссеру.
        if (by == null || by.isBlank()) {
            return "director";
        }
        return by;
    }

    public enum Type {
        DIRECTOR,
        TITLE,
        ALL,
        NOTHING
    }


}
