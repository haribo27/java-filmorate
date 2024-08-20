package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.reviewRequest.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.reviewRequest.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewDto createReview(@RequestBody @Valid NewReviewRequest request) {
        return reviewService.saveReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@RequestBody @Valid UpdateReviewRequest request) {
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("{id}")
    public ReviewDto findReviewById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @GetMapping
    public List<ReviewDto> getFilmsReviewOrAll(@RequestParam(value = "filmId", required = false) Long filmId,
                                               @RequestParam(value = "count", defaultValue = "10") long count) {
        return reviewService.getFilmsReviewsOrAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addReviewLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteReviewLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteReviewDislike(id, userId);
    }
}
