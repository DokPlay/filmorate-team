package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yourteam.filmorate.dal.repositories.deletefilmRepository.DeleteFilmRepository;
import ru.yourteam.filmorate.repository.FilmRepository;

@Service
@RequiredArgsConstructor
public class DeleteFilmService {

    private final DeleteFilmRepository repository;
    private final FilmRepository filmRepository;


    @Transactional
    public void deleteFilmById(int filmId) {
        filmRepository.ensureFilmExists(filmId);
        // Удаляем фильм и связанные записи единым атомарным действием
        repository.deleteFilmById(filmId);
    }
}
