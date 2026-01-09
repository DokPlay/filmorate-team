package ru.yourteam.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "films")
public class Film {

    // Идентификатор фильма в таблице films
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Integer id;

    // Читаемое название фильма, обязательное поле с ограничением 255 символов
    @Column(name = "film_name", nullable = false, length = 255)
    private String name;

    // Краткое описание, соответствует ограничению 200 символов в БД
    @Column(length = 200)
    private String description;

    // Дата релиза, хранится как DATE
    @Column(name = "release_date")
    private LocalDate releaseDate;

    // Продолжительность фильма в минутах
    @Column
    private Integer duration;

    // Внешний ключ на возрастной рейтинг MPA
    @Column(name = "mpa_id")
    private Integer mpaId;

    // Сущность рейтинга, подгружается лениво и не изменяет mpa_id напрямую
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mpa_id", insertable = false, updatable = false)
    @JsonIgnore
    private Mpa mpa;

    // Жанры фильма через связующую таблицу film_genre
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "film_genre",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    // Список режиссеров из таблицы film_director; при сериализации не ходим по циклу
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "film_director",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    @JsonIgnore
    private Set<Director> directors = new HashSet<>();
}
