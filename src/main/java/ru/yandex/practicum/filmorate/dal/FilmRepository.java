package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmWithGenresDirectorsExtractor;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String BASE_SELECT_QUERY = """
            SELECT
                            u.id AS film_id,
                            u.name AS film_name,
                            u.description AS film_description,
                            u.release_date AS film_release_date,
                            u.duration AS film_duration,
                            u.rating AS film_rating_id,
                            r.name AS film_rating_name,
                            fg.genre_id AS film_genre_id,
                            g.name AS film_genre_name,""";

    private static final String FIND_ALL_QUERY = BASE_SELECT_QUERY +
            """
                        fi.user_id AS liked_by_user_id,
                        fd.director_id AS film_director_id,
                        d.name AS film_director_name
                    FROM films AS u
                    LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
                    LEFT JOIN genre AS g ON fg.genre_id = g.id
                    LEFT JOIN FILM_LIKES AS fi ON u.id = fi.film_id
                    LEFT JOIN rating AS r ON u.rating = r.id
                    LEFT JOIN FILM_DIRECTOR AS fd ON u.id = fd.film_id
                    LEFT JOIN directors AS d ON fd.director_id = d.id
                    """;

    private static final String FIND_POPULAR_FILMS = BASE_SELECT_QUERY +
            """
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
                    """;
    private static final String SELECT_ALL_DIRECTOR_FILM_BY = BASE_SELECT_QUERY +
            """
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

    private static final String FIND_COMMON_FILMS_QUERY = BASE_SELECT_QUERY +
            """
                        COUNT(l3.user_id) AS like_count
                    FROM films AS u
                    JOIN FILM_LIKES AS l1 ON u.id = l1.film_id
                    JOIN FILM_LIKES AS l2 ON u.id = l2.film_id
                    LEFT JOIN FILM_GENRE AS fg ON u.id = fg.film_id
                    LEFT JOIN genre AS g ON fg.genre_id = g.id
                    LEFT JOIN FILM_LIKES AS l3 ON u.id = l3.film_id
                    LEFT JOIN rating AS r ON u.rating = r.id
                    WHERE l1.user_id = ? AND l2.user_id = ?
                    GROUP BY u.id, r.name
                    ORDER BY like_count DESC;
                    """;

    private static final String FIND_RECOMMENDED_FILMS = "WITH SimilarUserFilms AS (" +
            "    SELECT fl.FILM_ID FROM PUBLIC.FILM_LIKES fl WHERE fl.USER_ID = ?" +
            "), UserFilms AS (" +
            "    SELECT FILM_ID FROM PUBLIC.FILM_LIKES WHERE USER_ID = ?" +
            ")" +
            "SELECT u.ID AS film_id, " +
            "       u.NAME AS film_name, " +
            "       u.DESCRIPTION AS film_description, " +
            "       u.RELEASE_DATE AS film_release_date, " +
            "       u.DURATION AS film_duration, " +
            "       u.RATING AS film_rating_id, " +
            "       r.NAME AS film_rating_name, " +
            "       fg.GENRE_ID AS film_genre_id, " +
            "       g.NAME AS film_genre_name " +
            "FROM PUBLIC.FILMS u " +
            "JOIN SimilarUserFilms suf ON u.ID = suf.FILM_ID " +
            "LEFT JOIN UserFilms uf ON u.ID = uf.FILM_ID " +
            "LEFT JOIN PUBLIC.FILM_GENRE fg ON u.ID = fg.FILM_ID " +
            "LEFT JOIN PUBLIC.GENRE g ON fg.GENRE_ID = g.ID " +
            "LEFT JOIN PUBLIC.RATING r ON u.RATING = r.ID " +
            "WHERE uf.FILM_ID IS NULL;";

    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY +
            "WHERE u.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?," +
            " release_date = ?, duration = ?, rating = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String INSERT_FILM_LIKE = "INSERT INTO FILM_LIKES (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FROM_FILM = "DELETE FROM FILM_LIKES WHERE user_id = ? AND film_id = ?";

    private final FilmWithGenresDirectorsExtractor extractor;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmWithGenresDirectorsExtractor extractor) {
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
                film.getMpa().getId(),
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
    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        StringBuilder query = new StringBuilder(FIND_POPULAR_FILMS);
        if (genreId != null || year != null) {
            query.append(" WHERE ");
            if (genreId != null) {
                query.append("fg.genre_id = ").append(genreId);
            }
            if (year != null) {
                if (genreId != null) {
                    query.append(" AND ");
                }
                query.append("u.release_date LIKE ").append("'%").append(year).append("%'");
            }
        }
        String groupAndOrderSql = """
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
                ORDER BY like_count DESC""";
        query.append(groupAndOrderSql);
        if (count != null) {
            query.append(" LIMIT ").append(count);
        }
        return findMany(query.toString(), extractor);
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
            case "likes" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY + "ORDER BY like_count DESC", extractor, id);
            case "year" -> findMany(SELECT_ALL_DIRECTOR_FILM_BY + "ORDER BY film_release_date", extractor, id);
            default -> throw new EntityNotFoundException(String.format("Sored by %s not exist", sortBy));
        };
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return findMany(FIND_COMMON_FILMS_QUERY, extractor, userId, friendId);
    }

    public List<Film> getRecommendedFilms(long userId) {
        Long matchUserId;
        String findMatchUserIdSql = "WITH UserLikes AS (" +
                "    SELECT FILM_ID FROM PUBLIC.FILM_LIKES WHERE USER_ID = " + userId +
                "), MatchedUsers AS (" +
                "    SELECT fl.USER_ID AS user_id, COUNT(fl.FILM_ID) AS matched_likes" +
                "    FROM PUBLIC.FILM_LIKES fl" +
                "    JOIN UserLikes ul ON fl.FILM_ID = ul.FILM_ID" +
                "    WHERE fl.USER_ID <> " + userId +
                "    GROUP BY fl.USER_ID" +
                ")" +
                "SELECT user_id FROM MatchedUsers ORDER BY matched_likes DESC LIMIT 1;";
        try {
            matchUserId = jdbc.queryForObject(findMatchUserIdSql, Long.class);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
        return jdbc.query(FIND_RECOMMENDED_FILMS, new Object[]{matchUserId, userId}, extractor);
    }
}
