package ru.yourteam.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.dto.CommonFilmDtoDto;

import java.util.List;

@Slf4j
@Repository
public class CommonFilmRepository extends BaseRepository<CommonFilmDto> {

    private static final String FIND_COMMON_FILMS =
        "SELECT " +
            "    f.film_id AS id, " +
            "    f.film_name AS name, " +
            "    COALESCE(COUNT(l.user_id), 0) AS likes, " +
            "    EXTRACT(YEAR FROM f.release_date) AS release_year " +
            "FROM likes la " +
            "JOIN likes lb ON la.film_id = lb.film_id " +
            "    AND la.user_id = ? " +
            "    AND lb.user_id = ? " +
            "JOIN films f  ON f.film_id = la.film_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "GROUP BY f.film_id, f.film_name, f.release_date " +
            "ORDER BY likes DESC, f.film_id ASC " +
            "LIMIT ? OFFSET ?";

    public CommonFilmRepository(JdbcTemplate jdbc) {
        // маппер создаём сами, он не Spring-бин
        super(jdbc, new CommonFilmDtoDto());
    }

    public List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId,
                                                    int limit, int offset) {
        List<CommonFilmDto> commonLikedFilms =
            findMany(FIND_COMMON_FILMS, userId, friendId, limit, offset);
        log.info(
            "Получение общих фильмов двух пользователей {} и {}: total={}",
            userId,
            friendId,
            commonLikedFilms.size()
        );
        return commonLikedFilms;
    }
}
