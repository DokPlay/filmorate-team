package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yourteam.filmorate.model.Event;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;
import ru.yourteam.filmorate.repository.EventFeedRepository;
import ru.yourteam.filmorate.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventFeedService {

    private final EventFeedRepository eventFeedRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "eventFeed", key = "#userId")
    public Event writeEvent(int userId, EventType eventType, Operation operation, int entityId) {
        userRepository.ensureUserExists(userId);
        return eventFeedRepository.createEvent(userId, eventType, operation, entityId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "eventFeed", key = "#userId")
    public List<Event> getFeed(int userId) {
        userRepository.ensureUserExists(userId);
        return eventFeedRepository.findEventsByUserId(userId);
    }
}
