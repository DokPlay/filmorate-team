package ru.yourteam.filmorate.dal.repositories.deleteUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.model.User;
import ru.yourteam.filmorate.util.validation.UserValidation.UserValidator;

@Repository
@RequiredArgsConstructor
public class DeleteUserRepository {

    // JdbcTemplate и валидатор объявлены как финальные поля для надежной инъекции зависимостей
    private final JdbcTemplate jdbc;
    private final UserValidator validator;

    // Подготовленный запрос, который принимает идентификатор пользователя как параметр
    private static final String DELETE_FROM_USERS_QUERY = "DELETE FROM users WHERE user_id = ?";


    public void deleteUserById(int userId) {
        User user = validator.userValidation(userId);
        // Поскольку во всех таблицах ON DELETE CASCADE, удаляем только из users
        // Передаем идентификатор как параметр в подготовленный запрос, чтобы исключить SQL-инъекции
        jdbc.update(DELETE_FROM_USERS_QUERY, user.getId());
    }
}
