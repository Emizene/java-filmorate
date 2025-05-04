package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка валидации: дата релиза {} раньше минимальной {}", film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Запрос всех фильмов");
        List<Film> films = filmStorage.getAllFilms();
        log.info("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }

    public ResponseEntity<Film> addFilm(Film film) {
        log.debug("Попытка добавить фильм: {}", film.getName());

        boolean filmExists = filmStorage.getAllFilms().stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(film.getName()));
        if (filmExists) {
            log.warn("Фильм уже существует: {}", film.getName());
            throw new ValidationException("Этот фильм уже был добавлен");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        filmStorage.addFilm(film);
        log.info("Фильм успешно добавлен: ID={}, Название={}", film.getId(), film.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    public ResponseEntity<Film> updateFilm(Film film) {
        log.debug("Попытка обновить фильм ID={}", film.getId());

        List<Film> allFilms = filmStorage.getAllFilms();
        Film updateFilm = allFilms.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", film.getId());
                    return new ValidationException("Фильм с id " + film.getId() + " не найден");
                });

        if (film.getName() != null && !updateFilm.getName().equals(film.getName())) {
            boolean nameExists = allFilms.stream()
                    .anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()));
            if (nameExists) {
                log.warn("Фильм с таким названием уже существует: {}", film.getName());
                throw new ValidationException("Фильм с названием '" + film.getName() + "' уже существует");
            }
            log.debug("Обновление названия фильма с '{}' на '{}'", updateFilm.getName(), film.getName());
            updateFilm.setName(film.getName());
        }

        if (film.getDescription() != null && !film.getDescription().equals(updateFilm.getDescription())) {
            log.debug("Обновление описания фильма");
            updateFilm.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null && !film.getReleaseDate().equals(updateFilm.getReleaseDate())) {
            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            log.debug("Обновление даты релиза с {} на {}", updateFilm.getReleaseDate(), film.getReleaseDate());
            updateFilm.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null && !film.getDuration().equals(updateFilm.getDuration())) {
            if (film.getDuration() <= 0) {
                log.warn("Некорректная продолжительность: {}", film.getDuration());
                throw new ValidationException("Продолжительность должна быть положительной");
            }
            log.debug("Обновление продолжительности с {} на {}", updateFilm.getDuration(), film.getDuration());
            updateFilm.setDuration(film.getDuration());
        }

        filmStorage.updateFilm(updateFilm);
        log.info("Фильм успешно обновлен: ID={}", film.getId());
        return ResponseEntity.ok(updateFilm);
    }

    public ResponseEntity<Void> addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не может быть null");
        }

        Film film = filmStorage.getAllFilms().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        userStorage.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            log.warn("Повторный лайк: userID={} уже лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        log.info("Лайк добавлен: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не был введен");
        }

        Film film = filmStorage.getAllFilms().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        userStorage.getUserById(userId);

        if (!film.getLikes().remove(userId)) {
            log.warn("Лайк не найден: userID={} не лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь не ставил лайк этому фильму");
        }

        log.info("Лайк удален: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<Film>> getPopularFilms(int count) {
        log.debug("Запрос популярных фильмов: count={}", count);

        if (count <= 0) {
            log.warn("Некорректный параметр count: {}", count);
            throw new ValidationException("Параметр count должен быть положительным числом");
        }

        List<Film> popularFilms = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return ResponseEntity.ok(popularFilms);
    }

    public void deleteAllFilms() {
        log.warn("Удаление всех фильмов!");
        filmStorage.deleteAll();
        log.info("Все фильмы удалены");
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getAllFilms().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
