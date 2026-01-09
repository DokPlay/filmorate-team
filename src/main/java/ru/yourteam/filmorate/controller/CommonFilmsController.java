// Контроллер для выдачи списка общих фильмов у пользователей.
package ru.yourteam.filmorate.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.service.CommonFilmService;

@RestController
@RequestMapping("/films/common")
@RequiredArgsConstructor
public class CommonFilmsController {

    private final CommonFilmService commonFilmService;

    @GetMapping
    public List<CommonFilmDto> get(
        @RequestParam long userId,
        @RequestParam long friendId) {
        return commonFilmService.findCommonLikedFilms(userId, friendId);
    }
}
