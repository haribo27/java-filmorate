package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilm(long id);

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

}
