package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewDto {

    private long id;
    private String content;
    private boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;
}
