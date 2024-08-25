package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FilmDirectorRepository extends BaseRepository<Director> {

    private static final String INSERT_QUERY = "INSERT INTO FILM_DIRECTOR (film_id, director_id) VALUES (?,?)";
    private static final String DELETE_QUERY = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";

    public FilmDirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }


    public void saveDirector(Long filmId, List<Director> directors) {
        List<Director> filmDirector = new ArrayList<>(directors);
        this.jdbc.batchUpdate(
                INSERT_QUERY,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, String.valueOf(filmId));
                        ps.setLong(2, filmDirector.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return directors.size();
                    }
                });
    }

    public void deleteFilmsDirector(long filmId) {
        delete(DELETE_QUERY, filmId);
    }
}
