package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Getting all films");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.trace("Creating film: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Created new film: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.trace("Updating film: {}", film);
        if (film.getId() == null) {
            log.warn("ID can not be null");
            throw new ValidationException("Film id is null");
        }
        if (films.get(film.getId()) == null) {
            log.info("Film not found");
            throw new ValidationException("Film not found");
        }
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Current maxId: {}", currentMaxId);
        return ++currentMaxId;
    }
}
