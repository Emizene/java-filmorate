package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
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

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<FilmResponseDto>> findFilmsByDirectorSorted(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.findFilmsByDirectorSorted(directorId, sortBy);
    }
}
