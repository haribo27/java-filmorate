package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NotBeforeDate;


import java.time.LocalDate;
import java.util.Set;


@Data
public class Film {

    private Long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    @NotBlank
    private String description;
    @NotBeforeDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> likes;
}
