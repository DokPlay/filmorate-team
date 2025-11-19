package ru.yourteam.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yourteam.filmorate.dto.review.ReviewDto;
import ru.yourteam.filmorate.service.ReviewService;

import java.util.List;

// Контроллер для управления отзывами пользователей о фильмах.

@RestController
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
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        service.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable int id) {
        return service.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviewByFilmID(@RequestParam(required = false) Integer filmId,
                                             @RequestParam(defaultValue = "10") int count) {

        return service.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        return service.addLikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto removeLikeFromReview(@PathVariable int id, @PathVariable int userId) {
        return service.removeLikeFromReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        return service.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto removeDislikeFromReview(@PathVariable int id, @PathVariable int userId) {
        return service.removeDislikeFromReview(id, userId);
    }
}
