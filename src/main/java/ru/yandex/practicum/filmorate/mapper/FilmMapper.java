package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmRequest.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.filmRequest.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(MpaMapper.mapToMpa(request.getMpa()));
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream().map(GenreMapper::mapToGenre).collect(Collectors.toSet()));
        }
        if (request.getDirectors() != null) {
            film.setDirectors(request.getDirectors().stream().map(DirectorMapper::mapDirecorDtoToDirector).collect(Collectors.toSet()));
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(MpaMapper.mapMpaToMpaDto(film.getMpa()));
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setName(film.getName());
        filmDto.setGenres(film.getGenres().stream().map(GenreMapper::mapToGenreDto).collect(Collectors.toSet()));
        filmDto.setDirectors(film.getDirectors().stream().map(DirectorMapper::mapToDirectorDto).collect(Collectors.toSet()));
        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
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
        film.setMpa(MpaMapper.mapToMpa(request.getMpa()));
        if (request.getDirectors() == null) {
            film.setDirectors(Set.of());
        } else {
            film.setDirectors(request.getDirectors().stream().map(DirectorMapper::mapDirecorDtoToDirector).collect(Collectors.toSet()));
        }
        if (request.getGenres() == null) {
            film.setGenres(Set.of());
        } else {
            film.setGenres(request.getGenres().stream().map(GenreMapper::mapToGenre).collect(Collectors.toSet()));
        }
        return film;
    }
}
