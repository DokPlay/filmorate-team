package ru.yourteam.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.dto.CommonFilmDtoDto;

import java.util.List;

@Slf4j
@Service
public class CommonFilmRepository extends BaseRepository<CommonFilmDto> {

    private static final String FIND_COMMON_FILMS = "SELECT f.* " +
                                                    "FROM likes la " +
                                                    "JOIN likes lb ON la.film_id = lb.film_id " +
                                                    "AND la.user_id = ? " +
                                                    "AND lb.user_id = ? " +
                                                    "JOIN films f  ON f.id = la.film_id " +
                                                    "LEFT JOIN likes l ON l.film_id = f.id " +
                                                    "GROUP BY f.id " +
                                                    "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                                                    "LIMIT ? OFFSET ?";

    public CommonFilmRepository(JdbcTemplate jdbc, CommonFilmDtoDto mapper) {
        super(jdbc, mapper);
    }

    public List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId, int limit, int offset) {
        List<CommonFilmDto> commonLikedFilms = findMany(FIND_COMMON_FILMS, userId, friendId, limit, offset);
        log.info("Получение общих фильмов двух пользователей: {}", commonLikedFilms);
        return commonLikedFilms;
    }
}
