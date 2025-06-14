package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Valid
public class ReviewController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(@Valid @RequestBody ChangeReviewDto review) {
        return filmService.addReview(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        return filmService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return filmService.getReviewById(id);
    }

    @GetMapping
    public ResponseEntity<Set<ReviewResponseDto>> getReviewsToFilm(@RequestParam Long filmId) {
        return filmService.getReviewsToFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLikeOnReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> addDislikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addDislikeOnReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeFromReview(id, userId);
    }
}
