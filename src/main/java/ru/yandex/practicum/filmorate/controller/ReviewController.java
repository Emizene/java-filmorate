package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Valid
public class ReviewController {
    private final FilmService filmService;

    @PostMapping()
    public ResponseEntity<ReviewResponseDto> addReview(@Valid @RequestBody ChangeReviewDto review) {
        return filmService.addReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        filmService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return filmService.getReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<ReviewResponseDto> addLikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
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
