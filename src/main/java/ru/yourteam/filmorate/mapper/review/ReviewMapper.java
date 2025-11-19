package ru.yourteam.filmorate.mapper.review;

import ru.yourteam.filmorate.dto.review.ReviewDto;
import ru.yourteam.filmorate.model.Review;

public class ReviewMapper {

    public static Review mapToReview(ReviewDto reviewDto) {
        Review review = new Review();

        review.setReviewId(reviewDto.getReviewId());
        review.setContent(reviewDto.getContent());
        review.setPositive(reviewDto.isPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        review.setUseful(reviewDto.getUseful());

        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setPositive(review.isPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());

        return reviewDto;
    }
}
