package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorRepository extends BaseRepository<Director> {

    private static final String FIND_BY_ID_QUERY = "SELECT id, NAME FROM directors WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }


    public Director getById(long id) {
        Optional<Director> director =  findOne(FIND_BY_ID_QUERY, id);
        return director.get();
    }

    public Collection<Director> findAll() {
        Collection<Director> films = findMany(FIND_ALL_QUERY);
        log.info("Получение всех режиссеров: {}", films);
        return films;
    }

    public Director create(Director director) {
        return director;
    }

    public Director update(Director director) {
        return director;
    }

    public Director delete(Director director) {
        return director;
    }
}
