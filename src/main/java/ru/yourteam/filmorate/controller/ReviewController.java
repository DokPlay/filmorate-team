package ru.yourteam.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yourteam.filmorate.dto.review.ReviewDto;
import ru.yourteam.filmorate.service.ReviewService;

import java.util.List;

// Контроллер для управления отзывами пользователей о фильмах.

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
        ReviewDto createdReview = service.createReview(reviewDto);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto reviewDto) {
        return service.updateReview(reviewDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable @Positive int id) {
        service.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable @Positive int id) {
        return service.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviewByFilmID(@RequestParam(required = false) @Min(1) Integer filmId,
                                             @RequestParam(defaultValue = "10") @Positive int count) {

        return service.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLikeToReview(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return service.addLikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto removeLikeFromReview(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return service.removeLikeFromReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislikeToReview(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return service.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto removeDislikeFromReview(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return service.removeDislikeFromReview(id, userId);
    }
}
