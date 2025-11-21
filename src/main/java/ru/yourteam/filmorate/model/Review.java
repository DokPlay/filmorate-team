package ru.yourteam.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    // Первичный ключ для отзыва
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    // Текст отзыва хранится в TEXT и не может быть пустым
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Getter
    @Setter
    @Column(name = "is_positive", nullable = false)
    private boolean positive;

    // Внешний ключ на автора отзыва
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Внешний ключ на фильм, о котором отзыв
    @Column(name = "film_id", nullable = false)
    private Integer filmId;

    // Агрегированная полезность с дефолтом 0
    @Column(nullable = false)
    private Integer useful;

    // Ленивая ссылка на автора, не сериализуем чтобы избежать циклов
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    // Ленивая ссылка на фильм, не участвует в JSON-ответах
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    @JsonIgnore
    private Film film;

}
