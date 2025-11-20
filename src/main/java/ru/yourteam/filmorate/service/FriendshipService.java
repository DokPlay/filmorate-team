package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yourteam.filmorate.exception.NotFoundException;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;
import ru.yourteam.filmorate.repository.FriendshipRepository;
import ru.yourteam.filmorate.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final EventFeedService eventFeedService;

    @Transactional
    public void addFriend(int userId, int friendId) {
        validateUsers(userId, friendId);
        friendshipRepository.addFriend(userId, friendId);
        eventFeedService.writeEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    @Transactional
    public void removeFriend(int userId, int friendId) {
        validateUsers(userId, friendId);
        int removed = friendshipRepository.removeFriend(userId, friendId);
        if (removed == 0) {
            throw new NotFoundException("Связь дружбы между пользователями " + userId + " и " + friendId + " не найдена");
        }
        eventFeedService.writeEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    private void validateUsers(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        userRepository.ensureUserExists(userId);
        userRepository.ensureUserExists(friendId);
    }
}
