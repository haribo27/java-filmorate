package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void createFilm(Film film) {
        films.put(film.getId(), film);
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
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Map<Long, Film> getFilms() {
        return Map.copyOf(films);
    }
}
