package ru.yourteam.filmorate.dal.repositories.deleteUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.model.User;
import ru.yourteam.filmorate.util.validation.UserValidation.UserValidator;

@Repository
@RequiredArgsConstructor
public class DeleteUserRepository {

    JdbcTemplate jdbc;
    UserValidator validator;

    String DELETE_FROM_USERS_QUERY = "DELETE FROM users WHERE user_id = ?";


    public void deleteUserById(int userId) {
        User user = validator.userValidation(userId);
        //       Поскольку во всех таблицах ON DELETE CASCADE, удаляем только из users.
        jdbc.update(DELETE_FROM_USERS_QUERY);
    }
}
