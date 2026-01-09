package ru.yourteam.filmorate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "mpa")
public class Mpa {

    // Идентификатор рейтинга из таблицы mpa
    @Id
    @Column(name = "mpa_id")
    private Integer id;

    // Название возрастного рейтинга, уникальное и обязательное
    @Column(name = "mpa_name", nullable = false, length = 50, unique = true)
    private String name;
}
