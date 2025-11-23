package ru.yourteam.filmorate.dal.repositories.deletefilmRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteFilmRepository {

    private static final String DELETE_FROM_FILMS_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_FROM_REVIEW_QUERY = "DELETE FROM review WHERE film_id = ?";

    private final JdbcTemplate jdbc;

    public void deleteFilmById(int filmId) {
        // Поскольку во всех таблицах ON DELETE CASCADE, удаляем только из films и review.
        jdbc.update(DELETE_FROM_FILMS_QUERY, filmId);
        jdbc.update(DELETE_FROM_REVIEW_QUERY, filmId);
    }
}
