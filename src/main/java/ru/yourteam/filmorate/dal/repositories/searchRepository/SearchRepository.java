package ru.yourteam.filmorate.dal.repositories.searchRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yourteam.filmorate.dal.mappers.searchRowMapper.FilmSearchRowMapper;
import ru.yourteam.filmorate.dal.mappers.searchRowMapper.GenreRowMapper;
import ru.yourteam.filmorate.model.Film;
import ru.yourteam.filmorate.model.Genre;
import ru.yourteam.filmorate.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class SearchRepository {

    private final JdbcTemplate jdbc;
    private final FilmSearchRowMapper mapper;
    private final GenreRowMapper genreMapper;

    private static final String GET_ALL_SORTED_FILMS_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "WHERE f.film_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";


    private static final String GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "WHERE d.director_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "WHERE f.film_name LIKE ? AND d.director_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    public List<Film> getAllSortedByRatingFilms(SearchService.Type type, String by) {
        return switch (type) {
            case TITLE -> setGenresToFilm(GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY, buildSearchPattern(by));
            case DIRECTOR -> setGenresToFilm(GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY, buildSearchPattern(by));
            case ALL -> setGenresToFilm(
                GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY,
                buildSearchPattern(by),
                buildSearchPattern(by)
            );
            case NOTHING -> setGenresToFilm(GET_ALL_SORTED_FILMS_QUERY);
            default -> throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Неизвестный параметр запроса: " + by
            );
        };

    }

    private String buildSearchPattern(String by) {
        if (by == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поисковая строка не может быть пустой");
        }
        return by + "%";
    }

    private List<Film> setGenresToFilm(String query, Object... params) {
        List<Film> popularFilms = jdbc.query(query, mapper, params);
        if (popularFilms.isEmpty()) {
            return popularFilms;
        }

        List<Integer> filmsId = popularFilms.stream()
            .map(Film::getId)
            .collect(Collectors.toList());

        String placeholders = String.join(", ", java.util.Collections.nCopies(filmsId.size(), "?"));
        String popularFilmGenreQuery = "SELECT g.genre_id, g.genre_name, fg.film_id " +
            "FROM genres AS g " +
            "JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id IN (" + placeholders + ")";

        List<Genre> genresForPopularFilms = jdbc.query(popularFilmGenreQuery, genreMapper, filmsId.toArray());
        var genresByFilmId = genresForPopularFilms.stream().collect(Collectors.groupingBy(Genre::getFilmId));

        for (Film film : popularFilms) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new ArrayList<>()));
        }

        return popularFilms;
    }


}
