package ru.yourteam.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.CommonFilmDto;
import ru.yourteam.filmorate.repository.CommonFilmRepository;

import java.util.List;

@Slf4j
@Service
public class CommonFilmServiceImpl implements CommonFilmService {

    private final CommonFilmRepository commonFilmRepository;

    public CommonFilmServiceImpl(CommonFilmRepository commonFilmRepository) {
        this.commonFilmRepository = commonFilmRepository;
    }

    @Override
    public List<CommonFilmDto> findCommonLikedFilms(long userId, long friendId) {
        // TODO Проверить userId != friendId, существование обоих.

        return commonFilmRepository.findCommonLikedFilms(userId, friendId, 1000, 0);
    }
}
