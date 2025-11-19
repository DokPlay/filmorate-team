package ru.yourteam.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.dto.FilmDto;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.repository.DirectorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для управления режиссёрами и их фильмами.
 */
@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorServiceImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    public DirectorDto create(DirectorDto dto) {
        long id = directorRepository.insert(dto.getName());
        dto.setId(id);
        return dto;
    }

    @Override
    public DirectorDto update(DirectorDto dto) { // 200 — обновлённый объект; 404 — нет id; 400 — невалидный name
        if (dto.getId() == null) {
            throw new ValidationException("Не указан идентификатор режиссера для обновления");
        }
        // проверим, что режиссёр существует (бросит 404, если нет)
        getById(dto.getId());
        directorRepository.update(dto.getId(), dto.getName());
        return dto;
    }

    @Override
    public DirectorDto getById(long id) {
        Optional<DirectorDto> dto = directorRepository.getById(id);
        if (dto.isEmpty()) {
            throw new EntityNotFoundException("Режиссер с id: " + id + " не найден"); // вернем 404
        }
        return dto.get();
    }

    @Override
    public List<DirectorDto> getAll() {
        return directorRepository.getAll();
    }

    @Override
    public void deleteById(long id) {
        getById(id); // проверим есть ли режиссер
        directorRepository.deleteById(id);
    }

    public List<FilmDto> getFilmsByDirector(long directorId, SortMode sort) { // SortMode { LIKES, YEAR }
        getById(directorId); // проверим есть ли режиссер
        List<FilmDto> filmDtoList = new ArrayList<>();
        switch (sort) {
            case LIKES:
                filmDtoList = directorRepository.findFilmsByDirectorOrderByLikes(directorId, 1000, 0);
                break;
            case YEAR:
                filmDtoList = directorRepository.findFilmsByDirectorOrderByYear(directorId, 1000, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown sort mode: " + sort);
        }
        return filmDtoList;
    }
}
