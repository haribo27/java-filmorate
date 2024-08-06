package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class FilmWithGenresAndLikesExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("film_description"));
                film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
                film.setDuration(rs.getInt("film_duration"));

                Mpa mpa = new Mpa();
                mpa.setId(rs.getLong("film_rating_id"));
                mpa.setName(rs.getString("film_rating_name"));
                film.setMpa(mpa);

                film.setGenres(new LinkedHashSet<>());
                filmMap.put(filmId, film);
            }

            long genreId = rs.getLong("film_genre_id");
            if (genreId > 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("film_genre_name"));
                film.getGenres().add(genre);
            }
        }
        return new ArrayList<>(filmMap.values());
    }
}