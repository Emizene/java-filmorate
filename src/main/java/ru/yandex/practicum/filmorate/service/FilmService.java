package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final List<Film> allFilms = new ArrayList<>();
    private final Validator validator;

    public FilmService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage());
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(allFilms);
    }

    public ResponseEntity<Film> addFilm(Film film) {
        boolean filmExists = allFilms.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(film.getName()));
        if (filmExists) {
            throw new ValidationException("Этот фильм уже был добавлен");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        allFilms.add(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    public ResponseEntity<Film> updateFilm(Film film) {
        Film oldFilm = allFilms.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Фильм с id " + film.getId() + " не найден"));

        if (film.getName() != null && !film.getName().isBlank()
                && !oldFilm.getName().equals(film.getName())) {
            boolean nameExists = allFilms.stream()
                    .anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()));
            if (nameExists) {
                throw new ValidationException("Фильм с названием '" + film.getName() + "' уже существует");
            }
            oldFilm.setName(film.getName());
        }

        if (film.getDescription() != null && !film.getDescription().equals(oldFilm.getDescription())) {
            oldFilm.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null && !film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            oldFilm.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null && !film.getDuration().equals(oldFilm.getDuration())) {
            if (film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность должна быть положительной");
            }
            oldFilm.setDuration(film.getDuration());
        }

        return ResponseEntity.ok(oldFilm);
    }

    public void deleteAllFilms() {
        allFilms.clear();
    }

    private long getNextId() {
        long currentMaxId = allFilms.stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
