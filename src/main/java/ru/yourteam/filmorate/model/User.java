package ru.yourteam.filmorate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    // PK пользователя, соответствует user_id в таблице users
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    // Email хранится в VARCHAR(255) и не может быть null
    @Column(nullable = false, length = 255)
    private String email;

    // Логин, обязательное поле, аналогично ограничен 255 символами
    @Column(nullable = false, length = 255)
    private String login;

    // Пользовательское имя (user_name), допускается null
    @Column(name = "user_name", length = 255)
    private String name;

    // Дата рождения пользователя
    @Column
    private LocalDate birthday;
}
