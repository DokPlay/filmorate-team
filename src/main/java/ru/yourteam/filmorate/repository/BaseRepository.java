package ru.yourteam.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {

    /**
     * Общий доступ к {@link JdbcTemplate}, чтобы потомки могли переиспользовать одну и ту же настройку
     * подключения и единый {@link RowMapper}. Такой базовый слой упрощает сопровождение:
     * при смене стратегии маппинга или параметров пула достаточно скорректировать только конструктор.
     */
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    /**
     * Унифицированный способ получить одиночную сущность из БД.
     * Возвращает {@link Optional}, чтобы вызывающий код явно обрабатывал отсутствие данных
     * и не зависел от особенностей JDBC-исключений.
     */
    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Стандартный метод выборки множества строк с использованием подготовленного маппера.
     * Держим реализацию здесь, чтобы конкретные репозитории концентрировались на SQL,
     * а не на шаблонном коде обхода результатов.
     */
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }
}
