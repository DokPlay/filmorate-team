package ru.yourteam.filmorate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "directors")
public class Director {

    // PK режиссера с автоинкрементом
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long id;

    // Отображаемое имя режиссера
    @Column(name = "director_name", nullable = false, length = 255)
    private String name;
}
