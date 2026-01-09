// Сервисный слой для управления режиссёрами и их фильмами.
package ru.yourteam.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dto.FilmDto;
import ru.yourteam.filmorate.exception.ValidationException;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.repository.DirectorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {

    private static final int MAX_PAGE_SIZE = 500;
    // Верхний предел размера страницы, чтобы не отдавать слишком много данных за раз

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

    @Override
    public List<FilmDto> getFilmsByDirector(long directorId, SortMode sortMode, int limit, int offset) {
        // Сначала убеждаемся, что режиссер существует: выбрасываем 404, если его нет
        getById(directorId);

        int safeLimit = normalizeLimit(limit);
        validateOffset(offset);

        // Разносим обращение к репозиторию в отдельные ветки, чтобы проще было сопровождать
        return switch (sortMode) {
            case LIKES -> directorRepository.findFilmsByDirectorOrderByLikes(directorId, safeLimit, offset);
            case YEAR -> directorRepository.findFilmsByDirectorOrderByYear(directorId, safeLimit, offset);
        };
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            log.warn("Попытка сохранить режиссера с пустым именем");
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }

    private int normalizeLimit(int limit) {
        // Валидация и ограничение размера страницы от клиента
        if (limit <= 0) {
            throw new ValidationException("Параметр limit должен быть положительным");
        }
        if (limit > MAX_PAGE_SIZE) {
            log.info("Передан limit={} выше максимально допустимого {}; применяем ограничение", limit, MAX_PAGE_SIZE);
        }
        return Math.min(limit, MAX_PAGE_SIZE);
    }

    private void validateOffset(int offset) {
        // Отрицательные смещения не допускаем, чтобы не ломать контракт с БД
        if (offset < 0) {
            throw new ValidationException("Параметр offset не может быть отрицательным");
        }
    }
}
