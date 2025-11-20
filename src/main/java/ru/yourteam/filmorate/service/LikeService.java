package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.exception.NotFoundException;
import ru.yourteam.filmorate.model.EventType;
import ru.yourteam.filmorate.model.Operation;
import ru.yourteam.filmorate.repository.FilmRepository;
import ru.yourteam.filmorate.repository.LikeRepository;
import ru.yourteam.filmorate.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final EventFeedService eventFeedService;

    public void addLike(int filmId, int userId) {
        validateEntities(filmId, userId);
        likeRepository.addLike(filmId, userId);
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    public void removeLike(int filmId, int userId) {
        validateEntities(filmId, userId);
        int removed = likeRepository.removeLike(filmId, userId);
        if (removed == 0) {
            throw new NotFoundException("Лайк пользователя " + userId + " для фильма " + filmId + " не найден");
        }
        eventFeedService.writeEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    private void validateEntities(int filmId, int userId) {
        filmRepository.ensureFilmExists(filmId);
        userRepository.ensureUserExists(userId);
    }
}
