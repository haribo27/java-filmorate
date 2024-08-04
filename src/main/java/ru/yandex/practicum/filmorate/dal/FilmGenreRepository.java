package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Repository
public class FilmGenreRepository extends BaseRepository<Genre> {

    private static final String INSERT_QUERY = "INSERT INTO FILM_GENRE (film_id,genre_id) VALUES (?,?)";
    private final JdbcTemplate jdbcTemplate;


    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveGenre(long filmId, Set<Genre> genres) {
        genres.forEach(genre -> jdbcTemplate.update(INSERT_QUERY, filmId, genre.getId()));
    }
}
