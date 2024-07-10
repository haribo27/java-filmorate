package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void createFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(long id);

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

    long getNextId();
}
