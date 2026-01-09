package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yourteam.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    /**
     * Преднастроенный SQL, который повторно используется в нескольких местах.
     * Так мы избегаем дублирования строковых литералов и упрощаем модификацию запроса при миграциях схемы.
     */
    private static final String EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?);";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Публичная проверка наличия фильма с пробросом 404-ошибки на сервисный слой.
     * Слой репозиториев остаётся единым источником истины о том, что фильм существует,
     * а бизнес-логика может лаконично требовать данное условие без прямой работы с SQL.
     */
    public void ensureFilmExists(int filmId) {
        if (!existsById(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
    }

    /**
     * Базовая проверка существования записи. Возвращаем строго boolean, защитившись от возможного null
     * из {@link JdbcTemplate}, чтобы вызывающий код не занимался дополнительной валидацией результата.
     */
    public boolean existsById(int filmId) {
        Boolean exists = jdbcTemplate.queryForObject(EXISTS_QUERY, Boolean.class, filmId);
        return Boolean.TRUE.equals(exists);
    }
}
