package ru.yourteam.filmorate.controller;// Контроллер для поиска фильмов по названию или описанию.

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.dto.searchDto.FilmSearchDto;
import ru.yourteam.filmorate.service.SearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public List<FilmSearchDto> getAllSortedByRatingFilms(@RequestParam(required = false) String query,
                                                         @RequestParam(defaultValue = "director") String by) {
        return searchService.getAllSortedByRatingFilms(query, by);
    }


}
