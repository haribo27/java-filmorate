package ru.yandex.practicum.filmorate.dal.old;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 1;

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void addFilmLike(long userId, long filmId) {

    }

    @Override
    public void deleteLike(long userId, long filmId) {

    }

    public long getNextId() {
        return currentMaxId++;
    }
}
