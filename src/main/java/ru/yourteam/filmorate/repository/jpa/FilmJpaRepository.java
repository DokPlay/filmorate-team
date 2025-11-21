package ru.yourteam.filmorate.repository.jpa;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yourteam.filmorate.model.Film;

/**
 * JPA-репозиторий для выборки фильмов вместе со связанными сущностями без лавины N+1.
 */
public interface FilmJpaRepository extends JpaRepository<Film, Integer> {

    @Query("""
        SELECT DISTINCT f
        FROM Film f
        LEFT JOIN FETCH f.genres
        LEFT JOIN FETCH f.directors
        LEFT JOIN FETCH f.mpa
        WHERE f.id IN :ids
        """)
    List<Film> findByIdInWithRelations(@Param("ids") Collection<Integer> ids);
}

