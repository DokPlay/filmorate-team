package ru.yourteam.filmorate.dal.repositories.reviewRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dal.mappers.reviewRowMappers.ReviewRowMapper;
import ru.yourteam.filmorate.exception.NotFoundException; // commit: единый пакет исключений для корректной обработки 404
import ru.yourteam.filmorate.model.Review;
import ru.yourteam.filmorate.repository.FilmRepository;
import ru.yourteam.filmorate.repository.UserRepository;

import java.sql.PreparedStatement;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final JdbcTemplate jdbc;
    private final ReviewRowMapper rowMapper;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;


    String CREATE_REVIEW_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
        "VALUES (?,?,?,?,?)";
    String GET_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ? " +
        "WHERE review_id = ?";
    String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    String GET_REVIEW_BY_FILM_ID_QUERY =
        "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, " +
            "COALESCE(SUM(CASE WHEN rr.is_positive = true THEN 1 ELSE -1 END), 0) as useful " +
            "FROM reviews r " +
            "LEFT JOIN review_ratings rr ON r.review_id = rr.review_id " +
            "WHERE r.film_id = ? " +
            "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id " +
            "ORDER BY useful DESC LIMIT ?";
    String GET_ALL_SORTED_REVIEW =
        "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, " +
            "COALESCE(SUM(CASE WHEN rr.is_positive = true THEN 1 ELSE -1 END), 0) as useful " +
            "FROM reviews r " +
            "LEFT JOIN review_ratings rr ON r.review_id = rr.review_id " +
            "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id " +
            "ORDER BY useful DESC LIMIT ?";
    String ADD_LIKE_TO_REVIEW_QUERY = "INSERT INTO review_ratings (review_id, user_id, is_positive) VALUES (?,?,true)";
    String ADD_DISLIKE_TO_REVIEW_QUERY = "INSERT INTO review_ratings (review_id, user_id, is_positive) VALUES (?,?,false)";

    String DELETE_LIKE_FROM_REVIEW_QUERY = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? ";


    public Review getReviewById(int id) {
        try {
            Review review = jdbc.queryForObject(GET_REVIEW_BY_ID_QUERY, rowMapper, id);
            review.setUseful(getReviewUseful(id));
            return review;

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данный отзыв не найден.");
        }
    }


    public Review createReview(Review review) {
        Review createdReview = new Review();

        createdReview.setContent(review.getContent());
        createdReview.setPositive(review.isPositive());
        createdReview.setUserId(review.getUserId());
        createdReview.setFilmId(review.getFilmId());
        createdReview.setUseful(0);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement pr = con.prepareStatement(CREATE_REVIEW_QUERY, new String[]{"review_id"});
            pr.setString(1, createdReview.getContent());
            pr.setBoolean(2, createdReview.isPositive());
            pr.setInt(3, createdReview.getUserId());
            pr.setInt(4, createdReview.getFilmId());
            pr.setInt(5, createdReview.getUseful());
            return pr;
        }, keyHolder);

        createdReview.setReviewId(keyHolder.getKey().intValue());

        return createdReview;
    }

    public Review updateReview(Review review) {

        Review findReview = getReviewById(review.getReviewId());

        findReview.setContent(review.getContent());
        findReview.setPositive(review.isPositive());


        jdbc.update(UPDATE_REVIEW_QUERY, findReview.getContent(), findReview.isPositive(), findReview.getReviewId());

        return findReview;
    }

    public Review deleteReview(int id) {
        Review findReview = getReviewById(id);

        jdbc.update(DELETE_REVIEW_QUERY, id);

        return findReview;
    }

    public List<Review> getReviewsByFilmId(Integer filmId, int count) {
        if (filmId == null) {
            return jdbc.query(GET_ALL_SORTED_REVIEW, rowMapper, count);
        } else {
            filmRepository.ensureFilmExists(filmId);
            return jdbc.query(GET_REVIEW_BY_FILM_ID_QUERY, rowMapper, filmId, count);
        }

    }

    public Review addLikeToReview(int id, int userId) {
        userRepository.ensureUserExists(userId);
        Review review = getReviewById(id);

        //        Что бы небыло ошибок, если дважды лайк поставит, или поставит лайк и дизлайк.
        jdbc.update(DELETE_LIKE_FROM_REVIEW_QUERY, id, userId);

        jdbc.update(ADD_LIKE_TO_REVIEW_QUERY, id, userId);

        review.setUseful(getReviewUseful(id));
        return review;
    }

    public Review removeLikeFromReview(int id, int userId) {
        userRepository.ensureUserExists(userId);
        Review review = getReviewById(id);

        jdbc.update(DELETE_LIKE_FROM_REVIEW_QUERY, id, userId);

        review.setUseful(getReviewUseful(id));
        return review;
    }

    public Review addDislikeToReview(int id, int userId) {
        userRepository.ensureUserExists(userId);
        Review review = getReviewById(id);

        //        Такая же история.
        jdbc.update(DELETE_LIKE_FROM_REVIEW_QUERY, id, userId);

        jdbc.update(ADD_DISLIKE_TO_REVIEW_QUERY, id, userId);

        review.setUseful(getReviewUseful(id));
        return review;

    }

    public Review removeDislikeFromReview(int id, int userId) {
        userRepository.ensureUserExists(userId);
        Review review = getReviewById(id);

        jdbc.update(DELETE_LIKE_FROM_REVIEW_QUERY, id, userId);

        review.setUseful(getReviewUseful(id));
        return review;
    }

    private int getReviewUseful(int reviewId) {
        String sql = "SELECT COALESCE(SUM(CASE WHEN rr.is_positive = true THEN 1 ELSE -1 END), 0) as useful " +
            "FROM review_ratings rr WHERE rr.review_id = ?";
        return jdbc.queryForObject(sql, Integer.class, reviewId);
    }


}
