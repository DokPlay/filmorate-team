// Сервисный слой для управления режиссёрами и их фильмами.
package ru.yourteam.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.FilmDto;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.repository.DirectorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorServiceImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    public Director create(Director dto) {
        validateDirector(dto);
        long id = directorRepository.insert(dto.getName());
        dto.setId(id);
        return dto;
    }

    @Override
    public Director update(Director dto) { // 200 — обновлённый объект; 404 — нет id; 400 — невалидный name
        validateDirector(dto);
        // проверим, что режиссёр существует (бросит 404, если нет)
        getById(dto.getId());
        directorRepository.update(dto.getId(), dto.getName());
        return dto;
    }

    @Override
    public Director getById(long id) {
        Optional<Director> dto = directorRepository.getById(id);
        if (dto.isEmpty()) {
            throw new EntityNotFoundException("Режиссер с id: " + id + " не найден"); // вернем 404
        }
        return dto.get();
    }

    @Override
    public List<Director> getAll() {
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

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.warn("Попытка сохранить режиссера с пустым именем");
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }
}
