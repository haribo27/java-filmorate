package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class FilmDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private LinkedHashSet<Genre> genres;
    private Mpa mpa;
}
