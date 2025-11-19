package ru.yourteam.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Review {
    private int reviewId;
    private String content;
    @Getter
    @Setter
    private boolean positive;
    private int userId;
    private int filmId;
    private int useful;

}
