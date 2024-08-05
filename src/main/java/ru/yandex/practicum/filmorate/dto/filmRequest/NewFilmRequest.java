package ru.yandex.practicum.filmorate.dto.filmRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.NotBeforeDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {

    private long id;
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(min = 1, max = 200)
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotBeforeDate
    private LocalDate releaseDate;
    @Positive
    @NotNull
    private Integer duration;
    private Set<Genre> genres;
    @NotNull
    private Mpa mpa;
}
