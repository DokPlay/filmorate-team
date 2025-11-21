package ru.yourteam.filmorate.util.validation.UserValidation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dal.mappers.UserRowMapper;
import ru.yourteam.filmorate.exception.NotFoundException;
import ru.yourteam.filmorate.model.User;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final JdbcTemplate jdbc;
    private final UserRowMapper userMapper;

    public User userValidation(int id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbc.queryForObject(query, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данный пользователь отсутствует.");
        }
    }
}
