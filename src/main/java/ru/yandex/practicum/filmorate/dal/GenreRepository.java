package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_BY_ID_QUERY_GENRES = "SELECT * FROM genres WHERE id IN "
            + "(SELECT genre_id FROM film_genres WHERE film_id = ?)";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
    public List<Genre> getFilmGenres(Long filmId) {
        return findMany(FIND_BY_ID_QUERY_GENRES, filmId);
    }
}
