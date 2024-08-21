package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresAndLikesExtractor;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository("DirectorRepo")
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT
                d.id AS director_id,
                d.name AS director_name,
            FROM directors AS d
            """;
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY +
            "WHERE d.id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private final FilmWithGenresAndLikesExtractor extractor;

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper, FilmWithGenresAndLikesExtractor extractor) {
        super(jdbc, mapper);
        this.extractor = extractor;
    }

    @Override
    public Director createDirector(Director director) {
        throw new UnsupportedOperationException();
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
        return findMany(FIND_ALL_QUERY, extractor);
    }

    @Override
    public Optional<Director> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

}
