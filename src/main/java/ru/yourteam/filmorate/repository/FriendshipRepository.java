package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private static final String INSERT_FRIENDSHIP =
        "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?);";
    private static final String DELETE_FRIENDSHIP =
        "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?;";

    private final JdbcTemplate jdbcTemplate;

    public void addFriend(int userId, int friendId) {
        try {
            jdbcTemplate.update(INSERT_FRIENDSHIP, userId, friendId);
        } catch (DuplicateKeyException ignored) {
            // Повторное добавление дружбы не должно приводить к ошибке
        }
    }

    public int removeFriend(int userId, int friendId) {
        return jdbcTemplate.update(DELETE_FRIENDSHIP, userId, friendId);
    }
}
