package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.ReviewRepository;
import ru.yourteam.filmorate.dto.ReviewDto;
import ru.yourteam.filmorate.mappers.reviewMappers.ReviewMapper;

import java.util.List;
import java.util.stream.Collectors;


// Сервисный слой для операций с отзывами пользователей.
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewDto createReview(ReviewDto reviewDto) {
        return ReviewMapper.mapToReviewDto(reviewRepository.createReview(ReviewMapper.mapToReview(reviewDto)));
    }

    public ReviewDto updateReview(ReviewDto reviewDto) {
        return ReviewMapper.mapToReviewDto(reviewRepository.updateReview(ReviewMapper.mapToReview(reviewDto)));
    }

    public ReviewDto deleteReview(int id) {
        return ReviewMapper.mapToReviewDto(reviewRepository.deleteReview(id));
    }

    public ReviewDto getReviewById(int id) {
        return ReviewMapper.mapToReviewDto(reviewRepository.getReviewById(id));
    }

    public List<ReviewDto> getReviewsByFilmId(Integer filmId, int count) {
        return reviewRepository.getReviewsByFilmId(filmId, count).stream().
            map(ReviewMapper::mapToReviewDto).collect(Collectors.toList());
    }

    public ReviewDto addLikeToReview(int id, int userId) {
        return ReviewMapper.mapToReviewDto(reviewRepository.addLikeToReview(id, userId));
    }

    public ReviewDto removeLikeFromReview(int id, int userId) {
        return ReviewMapper.mapToReviewDto(reviewRepository.removeLikeFromReview(id, userId));
    }

    public ReviewDto addDislikeToReview(int id, int userId) {
        return ReviewMapper.mapToReviewDto(reviewRepository.addDislikeToReview(id, userId));
    }

    public ReviewDto removeDislikeFromReview(int id, int userId) {
        return ReviewMapper.mapToReviewDto(reviewRepository.removeDislikeFromReview(id, userId));
    }


}
