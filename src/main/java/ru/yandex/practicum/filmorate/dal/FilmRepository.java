package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresAndLikesExtractor;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
            LEFT JOIN directors AS d ON fd.director_id = g.id
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
            LEFT JOIN genre AS g ON fg.genre_id = g.id
            LEFT JOIN rating AS r ON u.rating = r.id
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
    private static final String SELECT_ALL_DIRECTOR_FILM_BY_LIKE = """
            SELECT f.*,
               mr.NAME AS map_name,
               array_agg(g.ID) AS genre_id,
               array_agg(g.NAME) AS genre_name,
               array_agg(ul.USER_ID) AS like_id,
               array_agg(d.ID) AS director_id,
               array_agg(d.NAME) AS director_name
               COUNT(DISTINCT ul.USER_ID) AS like_count
            FROM films AS f
            LEFT JOIN MPA_RATING AS mr ON f.MAP_RATING_ID = mr.ID
            LEFT JOIN FILM_GENRE AS fg ON f.ID = fg.FILM_ID
            LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.ID
            LEFT JOIN USER_LIKES AS ul ON ul.FILM_ID = f.ID
            LEFT JOIN DIRECTOR_FILM AS df ON df.FILM_ID = f.ID
            LEFT JOIN DIRECTOR AS d ON df.DIRECTOR_ID = d.ID
            WHERE d.ID = ?
            GROUP BY f.ID, mr.NAME
            ORDER BY like_count DESC
            """;
    private static final String SELECT_ALL_DIRECTOR_FILM_BY_YEAR = """
            SELECT f.*,
               mr.NAME AS map_name,
               array_agg(g.ID) AS genre_id,
               array_agg(g.NAME) AS genre_name,
               array_agg(ul.USER_ID) AS like_id,
               array_agg(d.ID) AS director_id,
               array_agg(d.NAME) AS director_name
            FROM films AS f
            LEFT JOIN MPA_RATING AS mr ON f.MAP_RATING_ID = mr.ID
            LEFT JOIN FILM_GENRE AS fg ON f.ID = fg.FILM_ID
            LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.ID
            LEFT JOIN USER_LIKES AS ul ON ul.FILM_ID = f.ID
            LEFT JOIN DIRECTOR_FILM AS df ON df.FILM_ID = f.ID
            LEFT JOIN DIRECTOR AS d ON df.DIRECTOR_ID = d.ID
            WHERE d.ID = ?
            GROUP BY f.ID, mr.NAME
            ORDER BY f.release_date
            """;
    private final FilmWithGenresAndLikesExtractor extractor;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmWithGenresAndLikesExtractor extractor) {
        super(jdbc, mapper);
        this.extractor = extractor;
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
    }

    @Override
    public void deleteFilm(long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_QUERY, extractor);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
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
        return switch (sortBy) {
            case "likes" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY_LIKE, id).stream().toList();
            case "year" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY_YEAR, id).stream().toList();
            default -> throw new EntityNotFoundException(String.format("Sored by %s not exist", sortBy));
        };
    }
}
