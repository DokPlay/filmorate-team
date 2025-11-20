package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private static final String EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?);";

    private final JdbcTemplate jdbcTemplate;

    public void ensureUserExists(int userId) {
        if (!existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public boolean existsById(int userId) {
        Boolean exists = jdbcTemplate.queryForObject(EXISTS_QUERY, Boolean.class, userId);
        return Boolean.TRUE.equals(exists);
    }
}
