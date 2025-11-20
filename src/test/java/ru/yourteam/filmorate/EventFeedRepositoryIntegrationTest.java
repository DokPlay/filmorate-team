package ru.yourteam.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yourteam.filmorate.model.Event;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;
import ru.yourteam.filmorate.repository.EventFeedRepository;

/**
 * Интеграционный тест репозитория ленты событий на реальной базе H2 + миграциях.
 */
public class EventFeedRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final int USER_ID = TEST_USER_MIN_ID;
    private static final int FRIEND_ID = TEST_USER_MIN_ID + 1;
    private static final int OTHER_USER_ID = TEST_USER_MIN_ID + 2;

    @Autowired
    private EventFeedRepository eventFeedRepository;

    @Test
    void findEventsByUserId_returnsEventsFromUserAndFriendsOnly() {
        insertTestUser(USER_ID, "user@test.ru", "user");
        insertTestUser(FRIEND_ID, "friend@test.ru", "friend");
        insertTestUser(OTHER_USER_ID, "other@test.ru", "other");

        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)", USER_ID, FRIEND_ID);

        insertEvent(USER_ID, EventType.REVIEW, Operation.ADD, 101, 1_000L);
        insertEvent(FRIEND_ID, EventType.LIKE, Operation.ADD, 202, 2_000L);
        insertEvent(OTHER_USER_ID, EventType.REVIEW, Operation.ADD, 303, 3_000L);

        List<Event> feed = eventFeedRepository.findEventsByUserId(USER_ID);

        assertThat(feed)
            .as("В ленте должны быть события пользователя и его друга")
            .hasSize(2);

        assertThat(feed.getFirst().getUserId())
            .as("Первым идёт самое позднее событие друга")
            .isEqualTo(FRIEND_ID);
        assertThat(feed.getLast().getUserId())
            .as("Событие не-друга не попадает в выборку")
            .isEqualTo(USER_ID);
    }

    private void insertEvent(int userId, EventType eventType, Operation operation, int entityId, long timestamp) {
        jdbcTemplate.update(
            "INSERT INTO events (user_id, event_type, operation, entity_id, event_timestamp) VALUES (?, ?, ?, ?, ?)",
            userId,
            eventType.name(),
            operation.name(),
            entityId,
            timestamp
        );
    }
}
