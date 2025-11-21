package ru.yourteam.filmorate.dal.repositories.deletefilmRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.util.validation.FilmValidation.FilmValidator;

@Repository
@RequiredArgsConstructor
public class DeleteFilmRepository {

    JdbcTemplate jdbc;
    FilmValidator validator;

    String DELETE_FROM_FILMS_QUERY = "DELETE FROM films WHERE film_id = ?";
    String DELETE_FROM_REVIEW_QUERY = "DELETE FROM review WHERE film_id = ?";


    public void deleteFilmById(int filmId) {

        Film film = validator.filmValidation(filmId);
//        Поскольку во всех таблицах ON DELETE CASCADE, удаляем только из films и review.
        jdbc.update(DELETE_FROM_FILMS_QUERY);
        jdbc.update(DELETE_FROM_REVIEW_QUERY);

    }

}
