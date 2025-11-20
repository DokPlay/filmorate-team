package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dal.mappers.EventRowMapper;
import ru.yourteam.filmorate.exception.NotFoundException;
import ru.yourteam.filmorate.model.Event;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EventFeedRepository {

    private static final String INSERT_EVENT_QUERY =
        "INSERT INTO events (user_id, event_type, operation, entity_id, event_timestamp) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_USER_QUERY =
        "SELECT * FROM events WHERE user_id = ? ORDER BY event_timestamp DESC, event_id DESC";

    private static final String FIND_USER_QUERY = "SELECT 1 FROM users WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public Event createEvent(int userId, EventType eventType, Operation operation, int entityId) {
        ensureUserExists(userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        long timestamp = System.currentTimeMillis();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                INSERT_EVENT_QUERY,
                Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, eventType.name());
            statement.setString(3, operation.name());
            statement.setInt(4, entityId);
            statement.setLong(5, timestamp);
            return statement;
        }, keyHolder);

        Event event = new Event();
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        event.setTimestamp(timestamp);
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        return event;
    }

    public List<Event> findEventsByUserId(int userId) {
        ensureUserExists(userId);
        return jdbcTemplate.query(FIND_BY_USER_QUERY, eventRowMapper, userId);
    }

    private void ensureUserExists(int userId) {
        try {
            jdbcTemplate.queryForObject(FIND_USER_QUERY, Integer.class, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}
