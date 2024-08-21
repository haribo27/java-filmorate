package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.reviewRequest.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.reviewRequest.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setUseful(request.getUseful());
        review.setPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getId());
        dto.setContent(review.getContent());
        dto.setUseful(review.getUseful());
        dto.setIsPositive(review.isPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        return dto;
    }

    public static Review mapToUpdatedReview(Review review, UpdateReviewRequest request) {
        review.setUseful(request.getUseful());
        review.setContent(request.getContent());
        review.setFilmId(request.getFilmId());
        review.setUserId(request.getUserId());
        review.setPositive(request.getIsPositive());
        return review;
    }
}
