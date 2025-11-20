package ru.yourteam.filmorate.repository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yourteam.filmorate.mapper.search.FilmSearchRowMapper;
import ru.yourteam.filmorate.mapper.search.GenreRowMapper;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.model.Genre;
import ru.yourteam.filmorate.service.SearchService;

@RequiredArgsConstructor
@Repository
public class SearchRepository {

    private static final String GET_ALL_SORTED_FILMS_QUERY =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "FROM films f "
            + "JOIN mpa m ON f.mpa_id = m.mpa_id "
            + "JOIN film_director fd ON f.film_id = fd.film_id "
            + "JOIN directors d ON fd.director_id = d.director_id "
            + "JOIN likes l ON f.film_id = l.film_id "
            + "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "FROM films f "
            + "JOIN mpa m ON f.mpa_id = m.mpa_id "
            + "JOIN film_director fd ON f.film_id = fd.film_id "
            + "JOIN directors d ON fd.director_id = d.director_id "
            + "JOIN likes l ON f.film_id = l.film_id "
            + "WHERE f.film_name LIKE ? "
            + "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "FROM films f "
            + "JOIN mpa m ON f.mpa_id = m.mpa_id "
            + "JOIN film_director fd ON f.film_id = fd.film_id "
            + "JOIN directors d ON fd.director_id = d.director_id "
            + "JOIN likes l ON f.film_id = l.film_id "
            + "WHERE d.director_name LIKE ? "
            + "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY =
        "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "FROM films f "
            + "JOIN mpa m ON f.mpa_id = m.mpa_id "
            + "JOIN film_director fd ON f.film_id = fd.film_id "
            + "JOIN directors d ON fd.director_id = d.director_id "
            + "JOIN likes l ON f.film_id = l.film_id "
            + "WHERE f.film_name LIKE ? AND d.director_name LIKE ? "
            + "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, "
            + "d.director_id, d.director_name "
            + "ORDER BY COUNT(l.user_id) DESC";

    private final JdbcTemplate jdbc;
    private final FilmSearchRowMapper filmSearchRowMapper;
    private final GenreRowMapper genreRowMapper;

    public List<Film> getAllSortedByRatingFilms(SearchService.Type type, String query) {
        return switch (type) {
            case TITLE -> fetchFilmsWithGenres(GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY, likePattern(query));
            case DIRECTOR -> fetchFilmsWithGenres(GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY, likePattern(query));
            case ALL -> fetchFilmsWithGenres(
                GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY,
                likePattern(query),
                likePattern(query)
            );
            case NOTHING -> fetchFilmsWithGenres(GET_ALL_SORTED_FILMS_QUERY);
            default -> throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Неизвестный параметр запроса: " + query
            );
        };
    }

    private List<Film> fetchFilmsWithGenres(String query, Object... params) {
        List<Film> films = params.length == 0
            ? jdbc.query(query, filmSearchRowMapper)
            : jdbc.query(query, filmSearchRowMapper, params);

        if (films.isEmpty()) {
            return films;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        String placeholders = filmIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String genreQuery =
            "SELECT g.genre_id, g.genre_name, fg.film_id "
                + "FROM genres AS g "
                + "JOIN film_genre AS fg ON g.genre_id = fg.genre_id "
                + "WHERE fg.film_id IN (" + placeholders + ")";

        List<Genre> genresForFilms = jdbc.query(genreQuery, genreRowMapper, filmIds.toArray());
        for (Film film : films) {
            film.setGenres(genresForFilms.stream()
                .filter(genre -> genre.getFilmId() == film.getId())
                .collect(Collectors.toList()));
        }
        return films;
    }

    private String likePattern(String value) {
        return value + "%";
    }
}
