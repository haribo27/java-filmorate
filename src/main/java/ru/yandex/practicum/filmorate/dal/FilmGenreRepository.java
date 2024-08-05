package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilmGenreRepository extends BaseRepository<Genre> {

    private static final String INSERT_QUERY = "INSERT INTO FILM_GENRE (film_id,genre_id) VALUES (?,?)";
    private final JdbcTemplate jdbcTemplate;


    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    public int[] saveGenre(long filmId, List<Genre> genres) {
        return this.jdbcTemplate.batchUpdate(
                INSERT_QUERY,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, String.valueOf(filmId));
                        ps.setLong(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }
}
