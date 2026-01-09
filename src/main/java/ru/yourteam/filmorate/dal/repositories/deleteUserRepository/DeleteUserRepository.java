package ru.yourteam.filmorate.dal.repositories.deleteUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteUserRepository {

    // JdbcTemplate объявлен как финальное поле для надежной инъекции зависимости
    private final JdbcTemplate jdbc;

    // Подготовленный запрос, который принимает идентификатор пользователя как параметр
    private static final String DELETE_FROM_USERS_QUERY = "DELETE FROM users WHERE user_id = ?";


    public void deleteUserById(int userId) {
        // Поскольку во всех таблицах ON DELETE CASCADE, удаляем только из users
        // Передаем идентификатор как параметр в подготовленный запрос, чтобы исключить SQL-инъекции
        jdbc.update(DELETE_FROM_USERS_QUERY, userId);
    }
}
