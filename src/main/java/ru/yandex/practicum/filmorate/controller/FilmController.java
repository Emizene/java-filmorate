package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Valid
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<List<FilmResponseDto>> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public ResponseEntity<FilmResponseDto> addFilm(@Valid @RequestBody ChangeFilmDto film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public ResponseEntity<FilmResponseDto> updateFilm(@Valid @RequestBody ChangeFilmDto film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmResponseDto>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmResponseDto> getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> addReview(@Valid @RequestBody ChangeReviewDto review) {
        return filmService.addReview(review);
    }

    @DeleteMapping("/reviews/{filmId}")
    public void deleteReview(@PathVariable Long filmId) {
        filmService.deleteReview(filmId);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return filmService.getReviewById(id);
    }

//    @PutMapping("/reviews/{id}/like/{userId}")
//    public ResponseEntity<Void> addLikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
//        return filmService.addLikeOnReview(id, userId);
//    }
//
//    @PutMapping("/reviews/{id}/dislike/{userId}")
//    public ResponseEntity<Void> addDislikeOnReview(@PathVariable Long id, @PathVariable Long userId) {
//        return filmService.addDislikeOnReview(id, userId);
//    }
//
//    @DeleteMapping("/reviews/{id}/like/{userId}")
//    public ResponseEntity<Void> deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
//        return filmService.deleteLikeFromReview(id, userId);
//    }

}
