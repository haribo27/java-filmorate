package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;


    public FilmService(FilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public void createFilm(Film film) {
        log.trace("Creating film: {}", film);
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
        checkFilmExistsOrGet(film.getId());
        inMemoryFilmStorage.updateFilm(film);
        log.info("Film updated: {}", film);
    }

    public void deleteFilm(long id) {
        log.trace("Deleting film with id: {}", id);
        checkFilmExistsOrGet(id);
        inMemoryFilmStorage.deleteFilm(id);
        log.info("Film deleted with id: {}", id);
    }

    public Collection<Film> getAllFilms() {
        log.info("Getting all films");
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilm(long id) {
        log.trace("Getting film with id: {}", id);
        return checkFilmExistsOrGet(id);
    }

    public void addLike(long filmId, long userId) {
        log.trace("Adding like with id: {}, user id {}", filmId, userId);
        checkFilmExistsOrGet(filmId);
        userService.getUserOrException(userId);
        getFilm(filmId).getLikes().add(userId);
        log.info("Added like with id: {}, user id {}", filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.trace("Deleting like from film {}, userId {}", filmId, userId);
        checkFilmExistsOrGet(filmId);
        userService.getUserOrException(userId);
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

    private Film checkFilmExistsOrGet(long id) {
        log.debug("Check film exist with id {}", id);
        return inMemoryFilmStorage.getFilm(id).orElseThrow(() ->
                new EntityNotFoundException("Фильм с таким id не найден"));

    }
}
