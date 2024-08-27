package ru.yandex.practicum.filmorate.dto.filmRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.validator.NotBeforeDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {

    @NotNull
    private long id;
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(min = 1, max = 200)
    @NotBlank
    private String description;
    @NotBeforeDate
    private LocalDate releaseDate;
    @Positive
    @NotNull
    private Integer duration;
    private Set<GenreDto> genres;
    @NotNull
    private MpaDto mpa;
    private Set<DirectorDto> directors;

    public boolean hasName() {
        return name.isBlank() || name.isEmpty();
    }

    public boolean hasDescription() {
        return description.isEmpty() || description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate == null;
    }

    public boolean hasDuration() {
        return duration == null;
    }
}
