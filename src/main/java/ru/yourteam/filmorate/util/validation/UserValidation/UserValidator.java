package ru.yourteam.filmorate.util.validation.UserValidation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yourteam.filmorate.dal.mappers.UserRowMapper;
import ru.yourteam.filmorate.exceptions.NotFoundException;
import ru.yourteam.filmorate.model.User;

@Component
public class UserValidator {

    JdbcTemplate jdbc;
    UserRowMapper userMapper;

    public User userValidation(int id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbc.queryForObject(query, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данный пользователь отсутствует.");
        }
    }
}
