package ru.yourteam.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.exception.GlobalExceptionHandler;
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
    public DirectorDto create(DirectorDto dto) {
        validateDirector(dto);
        long id = directorRepository.insert(dto.getName());
        dto.setId(id);
        return dto;
    }

    @Override
    public DirectorDto update(DirectorDto dto) { // 200 — обновлённый объект; 404 — нет id; 400 — невалидный name
        validateDirector(dto);
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
            case LIKES : filmDtoList = directorRepository.findFilmsByDirectorOrderByLikes(directorId, 1000, 0);
            case YEAR  : filmDtoList = directorRepository.findFilmsByDirectorOrderByYear(directorId, 1000,  0);
        }
        return filmDtoList;
    }

    private void validateDirector(DirectorDto director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.error("Передано пустое имя режиссера");
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }
}
