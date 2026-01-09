package ru.yourteam.filmorate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "genres")
public class Genre {

    // Техническое поле для выборок по фильмам, в таблице отсутствует
    @Transient
    private Integer filmId;

    // Идентификатор жанра из таблицы genres
    @Id
    @Column(name = "genre_id")
    private Integer id;

    // Название жанра, уникальное и обязательное
    @Column(name = "genre_name", nullable = false, length = 255, unique = true)
    private String name;
}
