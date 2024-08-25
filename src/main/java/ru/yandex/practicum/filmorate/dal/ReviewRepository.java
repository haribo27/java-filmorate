package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW = "INSERT INTO REVIEWS (content, is_positive," +
            " user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL = "SELECT * FROM REVIEWS";
    private static final String FIND_ALL_WITH_LIMIT = FIND_ALL + " ORDER BY useful DESC LIMIT ?";
    private static final String INSERT_REVIEWS_LIKE = "INSERT INTO REVIEWS_LIKES " +
            "(user_id, review_id, is_like) VALUES (?, ?, ?)";
    private static final String UPDATE_REVIEWS_LIKES_ON_DISLIKE = "UPDATE REVIEWS_LIKES SET IS_LIKE = false " +
            "where user_id = ? AND review_id = ?";
    private static final String UPDATE_REVIEW = "UPDATE REVIEWS SET content = ?, is_positive = ?, user_id = ?," +
            " film_id = ?, useful = ? WHERE id = ?";
    private static final String DELETE_REVIEW_LIKE = "DELETE FROM REVIEWS_LIKES WHERE " +
            "user_id = ? AND review_id = ? AND is_like = ?";
    private static final String UPDATE_REVIEWS_INCREMENT_USEFUL = "UPDATE REVIEWS SET useful = useful + 1 WHERE id = ?";
    private static final String UPDATE_REVIEWS_DECREMENT_USEFUL = "UPDATE REVIEWS SET useful = useful - 1 WHERE id = ?";
    private static final String ADD_DISLIKE_TO_REVIEW = "UPDATE REVIEWS SET useful = useful -1 WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE id = ?";
    private static final String FIND_REVIEW_BY_ID = FIND_ALL + " WHERE id = ?";
    private static final String FIND_ALL_FILM_REVIEWS = FIND_ALL + " WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
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

    @Override
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

    @Override
    public Optional<Review> findById(long id) {
        return findOne(FIND_REVIEW_BY_ID, id);
    }

    @Override
    public boolean deleteReview(long id) {
        return delete(DELETE_REVIEW, id);
    }

    @Override
    public List<Review> getAllReviews(long count) {
        return findMany(FIND_ALL_WITH_LIMIT, count);
    }

    @Override
    public List<Review> getFilmsReviews(Long filmId, long count) {
        return findMany(FIND_ALL_FILM_REVIEWS, filmId, count);
    }

    @Override
    public void addReviewLike(long id, long userId) {
        deleteReviewDislike(id, userId);
        jdbc.update(INSERT_REVIEWS_LIKE,
                userId,
                id,
                true);
        update(UPDATE_REVIEWS_INCREMENT_USEFUL, id);
    }

    @Override
    public void addReviewDislike(long id, long userId) {
        deleteReviewLike(id, userId);
        jdbc.update(
                UPDATE_REVIEWS_LIKES_ON_DISLIKE,
                userId,
                id);
        update(ADD_DISLIKE_TO_REVIEW, id);
    }

    @Override
    public int deleteReviewLike(long id, long userId) {
        int updatedRows = jdbc.update(DELETE_REVIEW_LIKE, userId, id, true);
        if (updatedRows > 0) update(UPDATE_REVIEWS_DECREMENT_USEFUL, id);
        return updatedRows;
    }

    @Override
    public int deleteReviewDislike(long id, long userId) {
        int updatedRows = jdbc.update(DELETE_REVIEW_LIKE, userId, id, false);
        if (updatedRows > 0) update(UPDATE_REVIEWS_INCREMENT_USEFUL, id);
        return updatedRows;
    }
}
