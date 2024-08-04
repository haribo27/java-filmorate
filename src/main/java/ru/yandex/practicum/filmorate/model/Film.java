package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NotBeforeDate;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;


@Data
public class Film {

    private long id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    @NotBlank
    @NotNull
    private String description;
    @NotBeforeDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private LinkedHashSet<Genre> genres;
    private Mpa mpa;
    private HashSet<Long> likes;
}
