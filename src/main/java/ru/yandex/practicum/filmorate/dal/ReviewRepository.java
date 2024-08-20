package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

    private static final String INSERT_REVIEW = "INSERT INTO REVIEWS (content, is_positive," +
            " user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL = "SELECT * FROM REVIEWS";
    private static final String INSERT_REVIEWS_LIKE = "INSERT INTO REVIEWS_LIKES " +
            "(user_id, review_id, is_like) VALUES (?, ?, ?)";
    private final String UPDATE_REVIEWS_LIKES_ON_DISLIKE = "UPDATE REVIEWS_LIKES SET IS_LIKE = false " +
            "where user_id = ? AND review_id = ?";
    private static final String UPDATE_REVIEW = "UPDATE REVIEWS SET content = ?, is_positive = ?, user_id = ?," +
            " film_id = ?, useful = ? WHERE id = ?";
    private static final String DELETE_REVIEW_LIKE = "DELETE FROM REVIEWS_LIKES WHERE " +
            "user_id = ? AND review_id = ? AND is_like = ?";
    private static final String UPDATE_REVIEWS_INCREMENT_USEFUL = "UPDATE REVIEWS SET useful = useful + 1 WHERE id = ?";
    private static final String UPDATE_REVIEWS_DECREMENT_USEFUL = "UPDATE REVIEWS SET useful = useful -1 WHERE id = ?";
    private static final String ADD_DISLIKE_TO_REVIEW = "UPDATE REVIEWS SET useful = useful -2 WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE id = ?";
    private static final String FIND_REVIEW_BY_ID = "SELECT * FROM REVIEWS WHERE id = ?";
    private static final String FIND_ALL_FILMS_REVIEWS = "SELECT * FROM REVIEWS WHERE film_id = ? LIMIT ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review saveReview(Review review) {
        long id = insert(
                INSERT_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setId(id);
        return review;
    }

    public void updateReview(Review review) {
        update(
                UPDATE_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId()
        );
    }

    public Optional<Review> findById(long id) {
        return findOne(FIND_REVIEW_BY_ID, id);
    }

    public boolean deleteReview(long id) {
        return delete(DELETE_REVIEW, id);
    }

    public List<Review> getAllReviews(long count) {
        return findMany(FIND_ALL, count);
    }

    public List<Review> getFilmsReviews(Long filmId, long count) {
        return findMany(FIND_ALL_FILMS_REVIEWS, filmId, count);
    }

    public void addReviewLike(long id, long userId) {
        jdbc.update(INSERT_REVIEWS_LIKE,
                userId,
                id,
                true);
        update(UPDATE_REVIEWS_INCREMENT_USEFUL, id);
    }

    public void addReviewDislike(long id, long userId) {
        jdbc.update(
                UPDATE_REVIEWS_LIKES_ON_DISLIKE,
                userId,
                id);
        update(ADD_DISLIKE_TO_REVIEW, id);
    }

    public int deleteReviewLike(long id, long userId) {
        int updatedRows = jdbc.update(DELETE_REVIEW_LIKE, userId, id, true);
        if (updatedRows > 0) update(UPDATE_REVIEWS_DECREMENT_USEFUL, id);
        return updatedRows;
    }

    public int deleteReviewDislike(long id, long userId) {
        int updatedRows = jdbc.update(DELETE_REVIEW_LIKE, userId, id, false);
        if (updatedRows > 0) update(UPDATE_REVIEWS_INCREMENT_USEFUL, id);
        return updatedRows;
    }
}
