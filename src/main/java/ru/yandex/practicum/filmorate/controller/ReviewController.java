package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Set;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Valid
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(@Valid @RequestBody ChangeReviewDto review) {
        return reviewService.addReview(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public ResponseEntity<Set<ReviewResponseDto>> getReviewsToFilm(@RequestParam(defaultValue = "10") int count,
                                                                   @RequestParam(required = false) Long filmId) {
        return reviewService.getReviewsToFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addLikeOnReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> addDislikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addDislikeOnReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> deleteDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.deleteDislikeFromReview(id, userId);
    }

    @PutMapping
    public ResponseEntity<ReviewResponseDto> updateReview(@Valid @RequestBody ChangeReviewDto review) {
        return reviewService.updateReview(review);
    }
}
