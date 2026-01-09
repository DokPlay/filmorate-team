package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.repository.CommonFilmRepository;
import ru.yourteam.filmorate.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonFilmServiceImpl implements CommonFilmService {

    private static final int DEFAULT_LIMIT = 1000;
    private static final int DEFAULT_OFFSET = 0;

    private final CommonFilmRepository commonFilmRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId) {
        validateUsers(userId, friendId);
        return commonFilmRepository.findCommonLikedFilms(userId, friendId, DEFAULT_LIMIT, DEFAULT_OFFSET);
    }

    private void validateUsers(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователи должны быть разными");
        }

        userRepository.ensureUserExists(Math.toIntExact(userId));
        userRepository.ensureUserExists(Math.toIntExact(friendId));
    }
}
