package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreMapper genreMapper;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewRatingRepository ratingRepository;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public ResponseEntity<List<FilmResponseDto>> getAllFilms() {
        log.debug("Запрос всех фильмов");
        List<FilmResponseDto> films = filmRepository.findAll().stream()
                .map(filmMapper::toFilmDto)
                .toList();

        log.info("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }

    @Transactional
    public ResponseEntity<FilmResponseDto> addFilm(ChangeFilmDto film) {
        log.debug("Попытка добавить фильм: {}", film.getName());

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpa() != null && (film.getMpa().getId() < 1 || film.getMpa().getId() > 5)) {
            throw new NotFoundException("Mpa рейтинга с ID %s не существует"
                    .formatted(film.getMpa().getId()));
        }

        if (film.getGenres() != null && film.getGenres().stream()
                .anyMatch(genre -> genre.getId() < 1 || genre.getId() > 6)) {
            throw new NotFoundException("Жанра с таким ID не существует");
        }

        Film entity = filmMapper.toEntity(film);
        filmRepository.save(entity);
        log.info("Фильм успешно добавлен: ID={}, Название={}", entity.getId(), entity.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(filmMapper.toFilmDto(entity));
    }

    @Transactional
    public ResponseEntity<FilmResponseDto> updateFilm(ChangeFilmDto film) {
        log.debug("Попытка обновить фильм ID={}", film.getId());

        List<Film> allFilms = filmRepository.findAll();
        Film updateFilm = allFilms.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", film.getId());
                    return new NotFoundException("Фильм с id " + film.getId() + " не найден");
                });

        if (film.getName() != null && !updateFilm.getName().equals(film.getName())) {
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

        if (film.getMpa() != null) {
            if (film.getMpa().getId() < 1 || film.getMpa().getId() > 5) {
                throw new NotFoundException("MPA рейтинга с ID %d не существует".formatted(film.getMpa().getId()));
            }

            Mpa mpaEntity = mpaRepository.findById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден"));

            if (updateFilm.getMpaRating() == null || !updateFilm.getMpaRating().getId().equals(mpaEntity.getId())) {
                log.debug("Обновление MPA рейтинга с {} на {}",
                        updateFilm.getMpaRating() != null ? updateFilm.getMpaRating().getId() : "null",
                        mpaEntity.getId());
                updateFilm.setMpaRating(mpaEntity);
            }
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(genreDto -> {
                if (genreDto.getId() < 1 || genreDto.getId() > 6) {
                    throw new NotFoundException("Жанра с ID %d не существует".formatted(genreDto.getId()));
                }
            });

            List<Genre> uniqueGenres = film.getGenres().stream()
                    .distinct()
                    .map(genreMapper::toEntity)
                    .toList();

            if (!uniqueGenres.equals(updateFilm.getGenres())) {
                log.debug("Обновление жанров фильма");
                updateFilm.setGenres(uniqueGenres);
            }
        }

        filmRepository.save(updateFilm);
        log.info("Фильм успешно обновлен: ID={}", film.getId());
        return ResponseEntity.ok().body(filmMapper.toFilmDto(updateFilm));
    }

    @Transactional
    public ResponseEntity<Void> addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не может быть null");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: ID={}", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        List<User> usersWithLikes = film.getUsersWithLikes();
        if (usersWithLikes.contains(user)) {
            log.warn("Повторный лайк: userID={} уже лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        usersWithLikes.add(user);
        filmRepository.save(film);
        log.info("Лайк добавлен: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не был введен");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: ID={}", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        List<User> usersWithLikes = film.getUsersWithLikes();
        if (!usersWithLikes.remove(user)) {
            log.warn("Лайк не найден: userID={} не лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь не ставил лайк этому фильму");
        }

        filmRepository.save(film);
        log.info("Лайк удален: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<FilmResponseDto>> getPopularFilms(int count) {
        log.debug("Запрос популярных фильмов: count={}", count);

        if (count <= 0) {
            log.warn("Некорректный параметр count: {}", count);
            throw new ValidationException("Параметр count должен быть положительным числом");
        }

        List<Film> popularFilms = filmRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersWithLikes().size()).reversed())
                .limit(count)
                .toList();

        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return ResponseEntity.ok(filmMapper.toFilmDtoList(popularFilms));
    }

    public ResponseEntity<FilmResponseDto> getFilmById(Long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID %s не найден".formatted(filmId)));

        log.info("Найден фильм: ID={}, Название={}", filmId, film.getName());

        return ResponseEntity.ok(filmMapper.toFilmDto(film));
    }

    @Transactional
    public ResponseEntity<ReviewResponseDto> addReview(ChangeReviewDto review) {
        log.info("Попытка добавить отзыв к фильму: {}", review.getFilmId());
        Optional<Review> oldReview = reviewRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId());
        if (oldReview.isPresent()) {
            throw new ValidationException("Пользователь с ID: %s уже оставил отзыв к фильму с ID: %s"
                    .formatted(review.getFilmId(), review.getUserId()));
        }
        Review entity = reviewMapper.toEntity(review);
        reviewRepository.save(entity);
        ratingRepository.save(ReviewRating.builder()
                .review(entity)
                .usersLikes(new HashSet<>())
                .usersDislikes(new HashSet<>())
                .build());
        log.info("Отзыв к фильму с ID: {} успешно добавлен", review.getFilmId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toReviewDto(entity));
    }

    @Transactional
    public ResponseEntity<Void> deleteReview(Long id) {
        log.debug("Попытка удалить отзыв: ID={}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID %s не найден".formatted(id)));

        reviewRepository.delete(review);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ReviewResponseDto> getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID %s не найден".formatted(id)));

        log.info("Найден отзыв: ID={}", id);

        return ResponseEntity.ok(reviewMapper.toReviewDto(review));
    }

    public ResponseEntity<Void> addLikeOnReview(Long id, Long userId) {
        log.debug("Попытка добавить лайк на отзыв: ID={}", id);
        ReviewRating reviewRating = ratingRepository.findByReviewId(id).orElseThrow(() -> {
            log.error("Отзыв не найден: ID={}", id);
            return new NotFoundException("Отзыв с id " + id + " не найден");
        });

        reviewRating.getUsersLikes().add(userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь не найден: ID={}", userId);
            return new NotFoundException("Пользователь с id " + userId + " не найден");
        }));

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> addDislikeOnReview(Long id, Long userId) {
        log.debug("Попытка добавить дизлайк на отзыв: ID={}", id);
        ReviewRating reviewRating = ratingRepository.findByReviewId(id).orElseThrow(() -> {
            log.error("Отзыв не найден: ID={}", id);
            return new NotFoundException("Отзыв с id " + id + " не найден");
        });

        reviewRating.getUsersDislikes().add(userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь не найден: ID={}", userId);
            return new NotFoundException("Пользователь с id " + userId + " не найден");
        }));

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteLikeFromReview(Long id, Long userId) {
        log.debug("Попытка удалить лайк с отзыва: ID={}", id);

        ReviewRating reviewRating = ratingRepository.findByReviewId(id)
                .orElseThrow(() -> {
                    log.error("Отзыв не найден: ID={}", id);
                    return new NotFoundException("Отзыв с id " + id + " не найден");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: ID={}", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        if (!reviewRating.getUsersLikes().remove(user)) {
            log.warn("Пользователь {} не ставил лайк отзыву {}", userId, id);
            throw new ValidationException("Пользователь не ставил лайк этому отзыву");
        }

        ratingRepository.save(reviewRating);
        log.info("Лайк пользователя {} удален с отзыва {}", userId, id);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void deleteAllFilms() {
        log.warn("Удаление всех фильмов");
        filmRepository.deleteAll();
        log.info("Все фильмы удалены");
    }

}
