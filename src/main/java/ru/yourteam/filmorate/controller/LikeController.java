package ru.yourteam.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films/{id}/like/{userId}")
public class LikeController {

    private final LikeService likeService;

    @PutMapping
    public void addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping
    public void removeLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        likeService.removeLike(filmId, userId);
    }
}
