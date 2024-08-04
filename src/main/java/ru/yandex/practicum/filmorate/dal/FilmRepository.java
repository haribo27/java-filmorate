package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresAndLikesExtractor;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository("FilmRepo")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT
                u.id AS film_id,
                u.name AS film_name,
                u.description AS film_description,
                u.release_date AS film_release_date,
                u.duration AS film_duration,
                u.rating AS film_rating_id,
                r.name AS film_rating_name,
                fg.genre_id AS film_genre_id,
                g.name AS film_genre_name,
                fi.user_id AS liked_by_user_id
            FROM films AS u
            LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
            LEFT JOIN genre AS g ON fg.genre_id = g.id
            LEFT JOIN FILM_LIKES AS fi ON u.id = fi.film_id
            LEFT JOIN rating AS r ON u.rating = r.id
            """;
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY +
            "WHERE u.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?," +
            " release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String INSERT_FILM_LIKE = "INSERT INTO FILM_LIKES (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FROM_FILM = "DELETE FROM FILM_LIKES WHERE user_id = ? AND film_id = ?";

    private final FilmGenreRepository filmGenreRepository;
    private final JdbcTemplate jdbcTemplate;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmGenreRepository filmGenreRepository, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.filmGenreRepository = filmGenreRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        if (film.getGenres() != null) {
            filmGenreRepository.saveGenre(id, film.getGenres());
        }
        film.setId(id);
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
    }

    @Override
    public void deleteFilm(long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> getAllFilms() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new FilmWithGenresAndLikesExtractor());
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void addFilmLike(long userId, long filmId) {
        update(INSERT_FILM_LIKE, userId, filmId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        update(DELETE_LIKE_FROM_FILM, userId, filmId);
    }
}