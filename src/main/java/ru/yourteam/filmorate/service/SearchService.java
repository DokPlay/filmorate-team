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
        if (query != null) {
            switch (by.toLowerCase()) {
                case "director" -> {
                    return searchRepository.getAllSortedByRatingFilms(Type.DIRECTOR, query).stream().map(FilmSearchMapper::mapToFilmSearchDto).
                        collect(Collectors.toList());
                }
                case "title" -> {
                    return searchRepository.getAllSortedByRatingFilms(Type.TITLE, query).stream().map(FilmSearchMapper::mapToFilmSearchDto).
                        collect(Collectors.toList());
                }
                case "director,title" -> {
                    return searchRepository.getAllSortedByRatingFilms(Type.ALL, query).stream().map(FilmSearchMapper::mapToFilmSearchDto).
                        collect(Collectors.toList());
                }
                default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Неизвестный параметр запроса: " + by
                );
            }
        }
        return searchRepository.getAllSortedByRatingFilms(Type.NOTHING, null).stream().map(FilmSearchMapper::mapToFilmSearchDto).
            collect(Collectors.toList());

    }

    public enum Type {
        DIRECTOR,
        TITLE,
        ALL,
        NOTHING
    }


}
