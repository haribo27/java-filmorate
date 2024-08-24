package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review saveReview(Review review);

    void updateReview(Review review);

    Optional<Review> findById(long id);

    boolean deleteReview(long id);

    List<Review> getAllReviews(long count);

    List<Review> getFilmsReviews(Long filmId, long count);

    void addReviewLike(long id, long userId);

    void addReviewDislike(long id, long userId);

    int deleteReviewLike(long id, long userId);

    int deleteReviewDislike(long id, long userId);
}
