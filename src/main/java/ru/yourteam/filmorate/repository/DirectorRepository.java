package ru.yourteam.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.dto.FilmDto;
import ru.yourteam.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorRepository extends BaseRepository<DirectorDto> {

    private static final RowMapper<DirectorDto> DIRECTOR_MAPPER = (rs, rowNum) -> {
        DirectorDto dto = new DirectorDto();
        dto.setId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        return dto;
    };

    private static final RowMapper<FilmDto> FILM_MAPPER = (rs, rowNum) -> {
        FilmDto dto = new FilmDto();
        dto.setId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setReleaseYear((Integer) rs.getObject("release_year"));
        dto.setDuration((Integer) rs.getObject("duration"));
        return dto;
    };

    private static final String FIND_BY_ID_QUERY =
        "SELECT id, name FROM directors WHERE id = ?";
    private static final String FIND_ALL_QUERY =
        "SELECT id, name FROM directors";
    private static final String DELETE_DIRECTOR =
        "DELETE FROM directors WHERE id = ?";
    private static final String DELETE_DIRECTOR_FILM_LINK =
        "DELETE FROM directors_films_link WHERE director_id = ?";
    private static final String UPDATE_DIRECTOR =
        "UPDATE directors SET name = ? WHERE id = ?";
    private static final String INSERT_QUERY =
        "INSERT INTO directors (name) VALUES (?)";

    private static final String FIND_FILMS_BY_LIKES =
        "SELECT f.* " +
            "FROM films f " +
            "JOIN film_director fd ON fd.film_id = f.film_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE fd.director_id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC, f.film_id ASC " +
            "LIMIT ? OFFSET ?";

    private static final String FIND_FILMS_BY_YEAR =
        "SELECT f.* " +
            "FROM films f " +
            "JOIN film_director fd ON fd.film_id = f.film_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE fd.director_id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY f.release_year DESC, f.film_id ASC " +
            "LIMIT ? OFFSET ?";

    public DirectorRepository(JdbcTemplate jdbc) {
        super(jdbc, DIRECTOR_MAPPER);
    }

    public Optional<DirectorDto> getById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<DirectorDto> getAll() {
        List<DirectorDto> directors = findMany(FIND_ALL_QUERY);
        log.info("Получение всех режиссеров: {}", directors);
        return directors;
    }

    public long insert(String name) {
        try {
            return insert(INSERT_QUERY, name);
        } catch (DataIntegrityViolationException e) {
            throw new InternalServerException("Ошибка при сохранении режиссера: " + e.getMessage());
        }
    }

    public int update(long id, String name) {
        return update(UPDATE_DIRECTOR, name, id);
    }

    public int deleteById(long id) {
        // сначала удаляем связи фильм–режиссёр
        delete(DELETE_DIRECTOR_FILM_LINK, id);
        // потом самого режиссёра
        return delete(DELETE_DIRECTOR, id);
    }

    public List<FilmDto> findFilmsByDirectorOrderByLikes(long directorId, int limit, int offset) {
        List<FilmDto> films = jdbc.query(
            FIND_FILMS_BY_LIKES,
            FILM_MAPPER,
            directorId,
            limit,
            offset
        );
        log.info("Получение фильмов режиссера {} по лайкам: {}", directorId, films);
        return films;
    }

    public List<FilmDto> findFilmsByDirectorOrderByYear(long directorId, int limit, int offset) {
        List<FilmDto> films = jdbc.query(
            FIND_FILMS_BY_YEAR,
            FILM_MAPPER,
            directorId,
            limit,
            offset
        );
        log.info("Получение фильмов режиссера {} по годам: {}", directorId, films);
        return films;
    }

    // Низкоуровневые помощники

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        return id != null ? id : 0;
    }

    protected int update(String query, Object... params) {
        return jdbc.update(query, params);
    }

    protected int delete(String query, Object... params) {
        return jdbc.update(query, params);
    }
}
