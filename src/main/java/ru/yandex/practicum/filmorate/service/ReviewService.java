package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.reviewRequest.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.reviewRequest.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public ReviewDto saveReview(NewReviewRequest request) {
        log.info("Saving new Review {}", request);
        Review review = ReviewMapper.mapToReview(request);
        review = reviewRepository.saveReview(review);
        log.info("Saved review {}", review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        log.info("Updating review {}", request);
        Review updatedReview = reviewRepository.findById(request.getReviewId())
                .map(review -> ReviewMapper.mapToUpdatedReview(review,request))
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с таким айди не найден"));
        reviewRepository.updateReview(updatedReview);
        log.info("Updated review {}", updatedReview);
        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void deleteReview(long id) {
        log.info("Deleting review with id {}",id);
        reviewRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Отзыв с таким айди не найден"));
        if (reviewRepository.deleteReview(id)) {
            log.info("Review deleted");
        } else  {
            log.info("Review not deleted");
        }

    }

    public ReviewDto findById(long id) {
        log.info("Finding Review by id {}",id);
        return reviewRepository.findById(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с таким айди не найден"));
    }

    public List<ReviewDto> getFilmsReviewsOrAll(Long filmId, long count) {
        log.info("Getting film's reviews or getting all reviews");
        if (filmId == null) {
            log.info("getting all reviews");
            return reviewRepository.getAllReviews(count)
                    .stream()
                    .map(ReviewMapper::mapToReviewDto).toList();
        } else {
            log.info("Getting reviews for film with id {}",filmId);
            return reviewRepository.getFilmsReviews(filmId,count)
                    .stream()
                    .map(ReviewMapper::mapToReviewDto).toList();
        }
    }

    public void addReviewLike(long id, long userId) {
        isReviewExist(id);
        isUserExist(userId);
        reviewRepository.addReviewLike(id,userId);
    }

    public void addReviewDislike(long id, long userId) {
        isReviewExist(id);
        isUserExist(userId);
        reviewRepository.addReviewDislike(id,userId);
    }

    public void deleteReviewLike(long id, long userId) {
        log.info("Deleting review like from review {} and user {}", id,userId);
        isReviewExist(id);
        isUserExist(userId);
        if (reviewRepository.deleteReviewLike(id,userId) > 0) {
            log.info("Review like is deleted");
        } else {
            log.info("On this review user does not has likes");
        }
    }

    public void deleteReviewDislike(long id, long userId) {
        log.info("Deleting review dislike from review {} and user {}",id,userId);
        isReviewExist(id);
        isUserExist(userId);
        if (reviewRepository.deleteReviewDislike(id,userId) > 0) {
            log.info("Review dislike is deleted");
        } else {
            log.info("On this review user does not has dislike");
        }
    }

    private void isReviewExist(long id) {
        reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с таким айди не найден"));
    }

    private void isUserExist(long userId) {
        userRepository.findUserById(userId)
                .orElseThrow(()-> new EntityNotFoundException("Юзера с таким айди не существует"));
    }
}
