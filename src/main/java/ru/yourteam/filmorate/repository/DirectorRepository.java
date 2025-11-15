package ru.yourteam.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.dto.DirectorDtoDto;
import ru.yourteam.filmorate.exception.InternalServerException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorRepository extends BaseRepository<DirectorDto> {

    private static final String FIND_BY_ID_QUERY = "SELECT id, NAME FROM directors WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";
    private static final String DELETE_DIRECTOR_FILM_LINK = "DELETE FROM directors_films_link WHERE director_id = ?";
    private static final String UPDATE_DIRECTOR = "UPDATE directors  SET name = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (NAME) VALUES (?)";
    private static final String FIND_FILMS_BY_LIKES = "SELECT f.* " +
                                                      "FROM films f " +
                                                      "JOIN film_director fd ON fd.film_id = f.id " +
                                                      "LEFT JOIN likes l     ON l.film_id  = f.id " +
                                                      "WHERE fd.director_id = ? " +
                                                      "GROUP BY f.id " +
                                                      "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                                                      "LIMIT ? OFFSET ?";
    private static final String FIND_FILMS_BY_YEAR = "SELECT f.* " +
                                                     "FROM films f " +
                                                     "JOIN film_director fd ON fd.film_id = f.id " +
                                                     "LEFT JOIN likes l      ON l.film_id = f.id " +
                                                     "WHERE fd.director_id = :directorId " +
                                                     "GROUP BY f.id " +
                                                     "ORDER BY f.release_year DESC, f.id ASC " +
                                                     "LIMIT ? OFFSET ?";

    public DirectorRepository(JdbcTemplate jdbc, DirectorDtoDto mapper) {
        super(jdbc, mapper);
    }


    public Optional<DirectorDto> getById(long id) {
        Optional<DirectorDto> director =  findOne(FIND_BY_ID_QUERY, id);
        return director;
    }

    public List<DirectorDto> getAll() {
        List<DirectorDto> director = findMany(FIND_ALL_QUERY);
        log.info("Получение всех режиссеров: {}", director);
        return director;
    }

    public long insert(String name) {

        long id;
        try {
            id = insert(
                INSERT_QUERY,
                    name);

        } catch (DataIntegrityViolationException e) {
            throw new InternalServerException("Ошибка при сохранении фильма: " + e.getMessage());
        }

        return id;
    }

    public int update(long id, String name) {

        return update(
                UPDATE_DIRECTOR,
                id,
                name
        );
    }

    public int deleteById(long id) {

        delete(DELETE_DIRECTOR_FILM_LINK, id); // удаление связь фильм-режиссер

        return delete(DELETE_DIRECTOR, id); // удаление режиссера
    }

    public List<FilmDto> findFilmsByDirectorOrderByLikes(long directorId, int limit, int offset) {
        List<FilmDto> directorByLikes = findMany(FIND_FILMS_BY_LIKES);
        log.info("Получение режиссеров по лайкам: {}", directorByLikes);
        return directorByLikes;
    }

    public List<FilmDto> findFilmsByDirectorOrderByYear(long directorId, int limit, int offset) {
        List<FilmDto> directorByYears = findMany(FIND_FILMS_BY_YEAR);
        log.info("Получение режиссеров по лайкам: {}", directorByYears);
        return directorByYears;
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps; }, keyHolder);

        int id = keyHolder.getKeyAs(Integer.class);

        return id;
    }

    protected int update(String query, Object... params) {
        return jdbc.update(query, params);
    }

    protected int delete(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        return rowsUpdated;
    }
}
