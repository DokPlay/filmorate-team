package ru.yourteam.filmorate.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.PopularFilmRepository;
import ru.yourteam.filmorate.dto.PopularFilmDto;
import ru.yourteam.filmorate.exception.ValidationException; // commit: используем единый пакет исключений

@Slf4j
@Service
@RequiredArgsConstructor
// Реализация сервисного слоя, инкапсулирующая бизнес-логику
// по выдаче популярных фильмов с дополнительными проверками входных данных.
public class PopularFilmServiceImpl implements PopularFilmService {

    private static final int DEFAULT_POPULAR_LIMIT = 10;
    private static final int MAX_POPULAR_LIMIT = 100;
    private static final int MIN_FILM_YEAR = 1895;

    private final PopularFilmRepository popularFilmRepository;

    @Override
    public List<PopularFilmDto> getMostPopular(int count, Integer genreId, Integer year) {
        // Сначала приводим запрошенное количество к допустимому диапазону,
        // чтобы ограничить нагрузку на базу и держать API предсказуемым.
        int normalizedCount = normalizeCount(count);

        // Проверяем фильтры до обращения к БД, чтобы сразу вернуть понятную ошибку
        // и не строить запрос с некорректными параметрами.
        validateFilters(genreId, year);

        List<PopularFilmDto> result =
                popularFilmRepository.findMostPopular(normalizedCount, genreId, year);

        log.info(
                "Found {} popular films (count={}, genreId={}, year={})",
                result.size(),
                normalizedCount,
                genreId,
                year
        );

        return result;
    }

    private int normalizeCount(int count) {
        // Входящее значение может быть неопределенным или слишком большим, поэтому
        // используем дефолтный лимит и верхнюю границу для контроля нагрузки.
        if (count <= 0) {
            return DEFAULT_POPULAR_LIMIT;
        }
        if (count > MAX_POPULAR_LIMIT) {
            return MAX_POPULAR_LIMIT;
        }
        return count;
    }

    private void validateFilters(Integer genreId, Integer year) {
        // Если указан год релиза, проверяем, что он не меньше даты появления кино.
        if (year != null && year < MIN_FILM_YEAR) {
            throw new ValidationException(
                    String.format(
                            "Год релиза не может быть меньше %d: %d",
                            MIN_FILM_YEAR,
                            year
                    )
            );
        }

        // Аналогичная проверка на валидность идентификатора жанра.
        if (genreId != null && genreId <= 0) {
            throw new ValidationException(
                    String.format(
                            "Идентификатор жанра должен быть положительным: %d",
                            genreId
                    )
            );
        }
    }
}
