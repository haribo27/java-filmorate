package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(long id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer count, Long genreId, Integer year);

    Optional<Film> findById(long id);

    void addFilmLike(long userId, long filmId);

    boolean isLikeExist(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    List<Film> getDirectorFilms(Long id, SortBy sortBy);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getRecommendedFilms(long userId);

    List<Film> searchFilmsByParams(String query, List<String> by);
}
