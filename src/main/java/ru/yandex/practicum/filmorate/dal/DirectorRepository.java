package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("DirectorRepo")
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY_BY_FILM_ID = """
            SELECT
            d.id AS director_id,
            d.name AS director_name
            FROM directors d
            JOIN film_director fd ON d.id = fd.director_id
            WHERE film_id = ? ORDER BY d.id
            """;
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY id";
    private static final String INSERT_FILM = "INSERT INTO film_director (film_id, director_id) VALUES(?,?)";
    private static final String GET_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES(?)";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director createDirector(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public void updateDirector(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
    }

    @Override
    public void deleteDirector(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public List<Director> getAllDirector() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> findById(long id) {
        return findOne(GET_BY_ID, id);
    }

    @Override
    public Collection<Director> findAllByFilmId(Long id) {
        return findMany(FIND_ALL_QUERY_BY_FILM_ID, id);
    }

    @Override
    public void insertIntoFilmDirector(long filmId, long directorId) {
        add(INSERT_FILM, filmId, directorId);
    }
}
