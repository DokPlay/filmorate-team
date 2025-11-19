package ru.yourteam.filmorate.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.dto.RecommendationDto;
import ru.yourteam.filmorate.service.RecommendationService;

/**
 * Контроллер для получения персональных рекомендаций фильмов.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * GET /users/{id}/recommendations
     * Возвращает список рекомендованных фильмов для пользователя.
     */
    @GetMapping("/{id}/recommendations")
    public List<RecommendationDto> getRecommendations(@PathVariable("id") int userId) {
        return recommendationService.getRecommendationsForUser(userId);
    }
}
