package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmRequest.FilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmRequest request) {
        return filmService.createFilm(request);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody FilmRequest request) {
        return filmService.updateFilm(request);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addFilmLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeFilmLike(id, userId);
    }
}
