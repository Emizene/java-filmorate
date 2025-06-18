package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Valid
public class FilmController {
    private final FilmService filmService;
    private final FilmMapper filmMapper;

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
    public ResponseEntity<List<FilmResponseDto>> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                                                 @RequestParam(required = false) Long genreId,
                                                                 @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmResponseDto> getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<FilmResponseDto>> findFilmsByDirectorSorted(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.findFilmsByDirectorSorted(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<Void> deleteFilmById(@PathVariable Long filmId) {
        filmService.deleteFilm(filmId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/common")
    public ResponseEntity<List<FilmResponseDto>> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FilmResponseDto>> searchFilms(@RequestParam(name = "query") String query,
                                                             @RequestParam(name = "by", defaultValue = "title") String by) {

        List<Film> films;
        List<String> searchBy = Arrays.asList(by.toLowerCase().split(","));

        if (searchBy.contains("director") && searchBy.contains("title")) {
            // Ищем по обоим полям
            films = filmService.searchFilmsByTitleOrDirector(query);
        } else if (searchBy.contains("director")) {
            // Ищем только по режиссеру
            films = filmService.searchFilmsByDirector(query);
        } else if (searchBy.contains("title")) {
            // Ищем только по названию
            films = filmService.searchFilmsByTitle(query);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<Film> sortedFilms = films.stream()
                .sorted(Comparator.comparing(Film::getId).reversed())
                .toList();
        List<FilmResponseDto> filmResponseDtos = sortedFilms.stream()
                .map(filmMapper::toFilmDto)
                .toList();
        return ResponseEntity.ok().body(filmResponseDtos);
    }
}
