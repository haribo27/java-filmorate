package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
        film.setDuration(rs.getInt("film_duration"));

        Mpa mpa = new Mpa();
        mpa.setId(Long.parseLong(rs.getString("film_rating_id")));
        mpa.setName(rs.getString("film_rating_name"));
        film.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        do {
            long genreId = rs.getLong("film_genre_id");
            if (genreId > 0) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("film_genre_id"));
                genre.setName(rs.getString("film_genre_name"));
                genres.add(genre);
            }
        } while (rs.next() && rs.getLong("film_id") == film.getId());
        film.setGenres(genres);
        return film;
    }
}
