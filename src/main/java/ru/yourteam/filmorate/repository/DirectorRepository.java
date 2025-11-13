package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.exception.InternalServerException;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorRepository extends BaseRepository<Director> {
    JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_ID_QUERY = "SELECT id, NAME FROM directors WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";
    private static final String DELETE_DIRECTOR_FILM_LINK = "DELETE FROM directors_films_link WHERE director_id = ?";
    private static final String UPDATE_DIRECTOR = "UPDATE directors  SET name = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (NAME) VALUES (?)";

    public DirectorRepository(JdbcTemplate jdbc, DirectorDto mapper) {
        super(jdbc, mapper);
        jdbcTemplate = jdbc;
    }


    public Director getById(long id) {
        Optional<Director> director =  findOne(FIND_BY_ID_QUERY, id);
        return director.get();
    }

    public Collection<Director> findAll() {
        Collection<Director> director = findMany(FIND_ALL_QUERY);
        log.info("Получение всех режиссеров: {}", director);
        return director;
    }

    public Director create(Director director) {

        validateDirector(director);

        long id;
        try {
            id = insert(
                INSERT_QUERY,
                director.getName());

        } catch (DataIntegrityViolationException e) {
            throw new InternalServerException("Ошибка при сохранении фильма: " + e.getMessage());
        }

        director.setId(id);

        return director;
    }

    public Director update(Director director) {

        validateDirector(director);

        update(
            UPDATE_DIRECTOR,
            director.getName()
        );
        return director;
    }

    public Director delete(long id) {
        Director director = getById(id);
        delete(DELETE_DIRECTOR_FILM_LINK, id); // удаление связь фильм-режиссер
        delete(DELETE_DIRECTOR, id);           // удаление режиссера
        return director;
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

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected void delete(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось удалить данные");
        }
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.error("Передано некорректное имя режиссера - {}", director.getName());
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }
}
