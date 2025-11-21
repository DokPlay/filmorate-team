package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.deletefilmRepository.DeleteFilmRepository;

@Service
@RequiredArgsConstructor
public class DeleteFilmService {

    DeleteFilmRepository repository;


    public void deleteFilmById(int filmId) {
        repository.deleteFilmById(filmId);
    }
}
