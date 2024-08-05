package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(long id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    Optional<Film> findById(long id);

    void addFilmLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

}
