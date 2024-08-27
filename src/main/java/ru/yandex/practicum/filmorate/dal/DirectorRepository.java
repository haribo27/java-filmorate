package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository("DirectorRepo")
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY id";
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
}
