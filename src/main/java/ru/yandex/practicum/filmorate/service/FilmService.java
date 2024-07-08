package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public void createFilm(Film film) {
        log.trace("Creating film: {}", film);
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        log.trace("Film id: {}", film.getId());
        inMemoryFilmStorage.createFilm(film);
        log.info("Created new film: {}", film);
    }

    public void updateFilm(Film film) {
        log.trace("Updating film: {}", film);
        if (film.getId() == null) {
            throw new ValidationException("Film id is null");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        checkFilmExists(film.getId());
        inMemoryFilmStorage.updateFilm(film);
        log.info("Film updated: {}", film);
    }

    public void deleteFilm(long id) {
        log.trace("Deleting film with id: {}", id);
        checkFilmExists(id);
        inMemoryFilmStorage.deleteFilm(id);
        log.info("Film deleted with id: {}", id);
    }

    public Collection<Film> getAllFilms() {
        log.info("Getting all films");
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilm(long id) {
        log.trace("Getting film with id: {}", id);
        checkFilmExists(id);
        return inMemoryFilmStorage.getFilms().get(id);
    }

    public void addLike(long filmId, long userId) {
        log.trace("Adding like with id: {}, user id {}", filmId, userId);
        checkFilmExists(filmId);
        userService.checkUserExist(userId);
        getFilm(filmId).getLikes().add(userId);
        log.info("Added like with id: {}, user id {}", filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.trace("Deleting like from film {}, userId {}", filmId, userId);
        checkFilmExists(filmId);
        userService.checkUserExist(userId);
        getFilm(filmId).getLikes().remove(userId);
        log.info("Deleted like from film {}, user {}", filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Getting popular films");
        return inMemoryFilmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void checkFilmExists(long id) {
        if (inMemoryFilmStorage.getFilms().get(id) == null) {
            throw new FilmNotFoundException("Фильма с такого id не существует");
        }
    }

    private long getNextId() {
        long currentMaxId = inMemoryFilmStorage.getFilms().keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Current maxId: {}", currentMaxId);
        return ++currentMaxId;
    }
}
