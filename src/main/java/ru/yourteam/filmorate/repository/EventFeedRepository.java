package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.dal.mappers.EventRowMapper;
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
        "SELECT e.* " +
            "FROM events e " +
            "WHERE e.user_id = ? " +
            "   OR e.user_id IN (" +
            "       SELECT f.friend_id FROM friendships f WHERE f.user_id = ? " +
            "       UNION " +
            "       SELECT f.user_id FROM friendships f WHERE f.friend_id = ? " +
            "   ) " +
            "ORDER BY e.event_timestamp DESC, e.event_id DESC";

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public Event createEvent(int userId, EventType eventType, Operation operation, int entityId) {
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
        return jdbcTemplate.query(FIND_BY_USER_QUERY, eventRowMapper, userId, userId, userId);
    }
}
