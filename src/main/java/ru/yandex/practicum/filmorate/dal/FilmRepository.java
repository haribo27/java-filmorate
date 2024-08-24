package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresAndLikesExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresLikesAndDirectorsExtractor;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
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
                fi.user_id AS liked_by_user_id,
                fd.director_id AS film_director_id
            FROM films AS u
            LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
            LEFT JOIN genre AS g ON fg.genre_id = g.id
            LEFT JOIN FILM_LIKES AS fi ON u.id = fi.film_id
            LEFT JOIN rating AS r ON u.rating = r.id
            LEFT JOIN FILM_DIRECTOR AS fd ON u.id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.id
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
    private static final String FIND_POPULAR_FILMS = """
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
               COUNT(fl.user_id) AS like_count
            FROM films AS u
            LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
            LEFT JOIN GENRE AS g ON fg.genre_id = g.id
            LEFT JOIN RATING AS r ON u.rating = r.id
            LEFT JOIN FILM_LIKES AS fl ON u.id = fl.film_id
            GROUP BY
                u.id,
                u.name,
                u.description,
                u.release_date,
                u.duration,
                u.rating,
                r.name,
                fg.genre_id,
                g.name
            ORDER BY like_count DESC
            LIMIT ?
            """;
    private static final String SELECT_ALL_DIRECTOR_FILM_BY = """
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
               fd.director_id AS film_director_id,
               d.name AS film_director_name,
               COUNT(fl.user_id) AS like_count
            FROM films AS u
            LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
            LEFT JOIN GENRE AS g ON fg.genre_id = g.id
            LEFT JOIN RATING AS r ON u.rating = r.id
            LEFT JOIN FILM_LIKES AS fl ON u.id = fl.film_id
            LEFT JOIN FILM_DIRECTOR AS fd ON u.id = fd.film_id
            LEFT JOIN DIRECTORS AS d ON d.id = fd.director_id
            WHERE fd.DIRECTOR_ID = ?
            GROUP BY
                u.id,
                u.name,
                u.description,
                u.release_date,
                u.duration,
                u.rating,
                r.name,
                fg.genre_id,
                g.name,
                fd.director_id,
                d.name
            """;
    private final FilmWithGenresAndLikesExtractor extractor;
    private final FilmWithGenresLikesAndDirectorsExtractor extractorDirector;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmWithGenresAndLikesExtractor extractor, FilmWithGenresLikesAndDirectorsExtractor extractorDirector) {
        super(jdbc, mapper);
        this.extractor = extractor;
        this.extractorDirector = extractorDirector;
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
        delete("DELETE FROM film_director WHERE film_id = ?", film.getId());
        for (Director director : film.getDirectors()) {
            update("INSERT INTO FILM_DIRECTOR (film_id, director_id) VALUES (?,?)", film.getId(), director.getId());
        }
    }

    @Override
    public void deleteFilm(long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_QUERY, extractor);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        return findMany(FIND_POPULAR_FILMS, extractor, count);
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

    @Override
    public List<Film> getDirectorFilms(Long id, String sortBy) {
        List<Film> films = switch (sortBy) {
            case "likes" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY + "ORDER BY like_count DESC",extractorDirector, id);
            case "year" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY + "ORDER BY film_release_date",extractorDirector, id);
            default -> throw new EntityNotFoundException(String.format("Sored by %s not exist", sortBy));
        };
        return films;
    }
}
