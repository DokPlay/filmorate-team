package ru.yourteam.filmorate.dal.repositories.deletefilmRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteFilmRepository {

    private static final String DELETE_FROM_FILMS_QUERY = "DELETE FROM films WHERE film_id = ?";

    private final JdbcTemplate jdbc;

    public void deleteFilmById(int filmId) {
        // Поскольку связанные таблицы используют ON DELETE CASCADE, достаточно удалить сам фильм.
        jdbc.update(DELETE_FROM_FILMS_QUERY, filmId);
    }
}
