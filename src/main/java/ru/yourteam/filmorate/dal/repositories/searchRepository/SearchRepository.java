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

    String GET_ALL_SORTED_FILMS_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "JOIN film_director fd ON f.film_id = fd.film_id " +
        "JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    String GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "JOIN film_director fd ON f.film_id = fd.film_id " +
        "JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "WHERE f.film_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";


    String GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "JOIN film_director fd ON f.film_id = fd.film_id " +
        "JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        "WHERE d.director_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    String GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "FROM films f " +
        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
        "JOIN film_director fd ON f.film_id = fd.film_id " +
        "JOIN directors d ON fd.director_id = d.director_id " +
        "JOIN likes l ON f.film_id = l.film_id " +
        // тут вопрос к ТЗ как может быть поиск одновременно по режисеру и по названию фильма.
        "WHERE f.film_name LIKE ? AND d.director_name LIKE ? " +
        "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
        "d.director_id, d.director_name " +
        "ORDER BY COUNT(l.user_id) DESC";

    public List<Film> getAllSortedByRatingFilms(SearchService.Type type, String by) {
        String str = by + "%";
        String str1 = str;
        switch (type) {
            case TITLE -> {
                return setGenresToFilm(GET_ALL_SORTED_FILMS_BY_FILM_NAME_QUERY, str);
            }
            case DIRECTOR -> {
                return setGenresToFilm(GET_ALL_SORTED_FILMS_BY_DIRECTOR_NAME_QUERY, str);
            }
            case ALL -> {
                return setGenresToFilm(GET_ALL_SORTED_FILMS_BY_FILM_NAME_AND_DIRECTOR_NAME_QUERY, str, str1);
            }
            case NOTHING -> {
                return setGenresToFilm(GET_ALL_SORTED_FILMS_QUERY, null);
            }
            default -> throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Неизвестный параметр запроса: " + by
            );
        }

    }

    private List<Film> setGenresToFilm(String query, String str) {
        List<Film> popularFilms = new ArrayList<>();
        if (str != null) {
            List<Film> withBy = jdbc.query(query, mapper, str);
            popularFilms.addAll(withBy);
        } else {
            List<Film> withOutBy = jdbc.query(query, mapper);
            popularFilms.addAll(withOutBy);
        }
        List<Integer> filmsId = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < popularFilms.size(); i++) {
            sb.append("?, ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        for (Film film : popularFilms) {
            filmsId.add(film.getId());
        }
        String popularFilmGenreQuery = "SELECT g.genre_id, g.genre_name, fg.film_id " +
            "FROM genres AS g " +
            "JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id IN (" + sb + ")";


        List<Genre> genresForPopularFilms = jdbc.query(popularFilmGenreQuery, genreMapper, filmsId.toArray());
        for (Film film : popularFilms) {
            film.setGenres(genresForPopularFilms.stream().filter(genre -> genre.getFilmId() == film.getId()).
                collect(Collectors.toList()));
        }

        return popularFilms;
    }

    private List<Film> setGenresToFilm(String query, String str1, String str2) {
        List<Film> popularFilms = jdbc.query(query, mapper, str1, str2);
        List<Integer> filmsId = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < popularFilms.size(); i++) {
            sb.append("?, ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        for (Film film : popularFilms) {
            filmsId.add(film.getId());
        }
        String popularFilmGenreQuery = "SELECT g.genre_id, g.genre_name, fg.film_id " +
            "FROM genres AS g " +
            "JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id IN (" + sb + ")";


        List<Genre> genresForPopularFilms = jdbc.query(popularFilmGenreQuery, genreMapper, filmsId.toArray());
        for (Film film : popularFilms) {
            film.setGenres(genresForPopularFilms.stream().filter(genre -> genre.getFilmId() == film.getId()).
                collect(Collectors.toList()));
        }

        return popularFilms;
    }


}
