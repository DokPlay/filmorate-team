package ru.yourteam.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.service.CommonFilmService;

import java.util.List;

/**
 * Контроллер для выдачи списка общих фильмов у пользователей.
 */
@RestController
@RequestMapping("/films/common")
public class CommonFilmsController {

    private final CommonFilmService commonFilmService;

    @Autowired
    public CommonFilmsController(CommonFilmService commonFilmService) {
        this.commonFilmService = commonFilmService;
    }

    @GetMapping
    public List<CommonFilmDto> get(
        @RequestParam long userId,
        @RequestParam long friendId) {
        return commonFilmService.findCommonLikedFilms(userId, friendId);
    }
}
