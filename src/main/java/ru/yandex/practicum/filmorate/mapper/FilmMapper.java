package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.dto.filmRequest.FilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(FilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());
        film.setGenres(request.getGenres());
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setName(film.getName());
        filmDto.setGenres(film.getGenres());
        return filmDto;
    }

    public static Film updateFilmFields(Film film, FilmRequest request) {
        if (!request.hasName()) {
            film.setName(request.getName());
        }
        if (!request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (!request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (!request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        return film;
    }
}
