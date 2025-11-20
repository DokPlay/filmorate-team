package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.reviewRepository.ReviewRepository;
import ru.yourteam.filmorate.dto.reviewDto.ReviewDto;
import ru.yourteam.filmorate.mappers.reviewMappers.ReviewMapper;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;

import java.util.List;
import java.util.stream.Collectors;


// Сервисный слой для операций с отзывами пользователей.
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventFeedService eventFeedService;

    public ReviewDto createReview(ReviewDto reviewDto) {
        ReviewDto createdReview = ReviewMapper.mapToReviewDto(
            reviewRepository.createReview(ReviewMapper.mapToReview(reviewDto)));
        eventFeedService.writeEvent(
            createdReview.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public ReviewDto updateReview(ReviewDto reviewDto) {
        ReviewDto updatedReview = ReviewMapper.mapToReviewDto(
            reviewRepository.updateReview(ReviewMapper.mapToReview(reviewDto)));
        eventFeedService.writeEvent(
            updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }

    public ReviewDto deleteReview(int id) {
        ReviewDto deletedReview = ReviewMapper.mapToReviewDto(reviewRepository.deleteReview(id));
        eventFeedService.writeEvent(
            deletedReview.getUserId(), EventType.REVIEW, Operation.REMOVE, deletedReview.getReviewId());
        return deletedReview;
    }

    public ReviewDto getReviewById(int id) {
        return ReviewMapper.mapToReviewDto(reviewRepository.getReviewById(id));
    }

    public List<ReviewDto> getReviewsByFilmId(Integer filmId, int count) {
        return reviewRepository.getReviewsByFilmId(filmId, count).stream().
            map(ReviewMapper::mapToReviewDto).collect(Collectors.toList());
    }

    public ReviewDto addLikeToReview(int id, int userId) {
        ReviewDto review = ReviewMapper.mapToReviewDto(reviewRepository.addLikeToReview(id, userId));
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.ADD, review.getReviewId());
        return review;
    }

    public ReviewDto removeLikeFromReview(int id, int userId) {
        ReviewDto review = ReviewMapper.mapToReviewDto(reviewRepository.removeLikeFromReview(id, userId));
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.REMOVE, review.getReviewId());
        return review;
    }

    public ReviewDto addDislikeToReview(int id, int userId) {
        ReviewDto review = ReviewMapper.mapToReviewDto(reviewRepository.addDislikeToReview(id, userId));
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.ADD, review.getReviewId());
        return review;
    }

    public ReviewDto removeDislikeFromReview(int id, int userId) {
        ReviewDto review = ReviewMapper.mapToReviewDto(reviewRepository.removeDislikeFromReview(id, userId));
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.REMOVE, review.getReviewId());
        return review;
    }


}
