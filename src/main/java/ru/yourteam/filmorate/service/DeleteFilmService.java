package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.deletefilmRepository.DeleteFilmRepository;
import ru.yourteam.filmorate.repository.FilmRepository;

@Service
@RequiredArgsConstructor
public class DeleteFilmService {

    private final DeleteFilmRepository repository;
    private final FilmRepository filmRepository;


    public void deleteFilmById(int filmId) {
        filmRepository.ensureFilmExists(filmId);
        repository.deleteFilmById(filmId);
    }
}
