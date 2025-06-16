package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewRatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final EventService eventService;

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
        eventService.createEvent(entity.getUser().getId(), EventType.REVIEW, EventOperation.ADD, entity.getId());

        log.info("Отзыв к фильму с ID: {} успешно добавлен", review.getFilmId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toReviewDto(entity));
    }

    @Transactional
    public ResponseEntity<Void> deleteReview(Long id) {
        log.debug("Попытка удалить отзыв: ID={}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID %s не найден".formatted(id)));

        ratingRepository.deleteById(id);
        reviewRepository.delete(review);
        eventService.createEvent(review.getUser().getId(), EventType.REVIEW, EventOperation.REMOVE, review.getId());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ReviewResponseDto> getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID %s не найден".formatted(id)));

        log.info("Найден отзыв: ID={}", id);

        return ResponseEntity.ok(reviewMapper.toReviewDto(review));
    }

    @Transactional
    public ResponseEntity<Void> addLikeOnReview(Long id, Long userId) {
        log.debug("Попытка добавить лайк на отзыв: ID={}", id);
        ReviewRating reviewRating = ratingRepository.findByReviewId(id).orElseThrow(() -> {
            log.error("Отзыв не найден: ID={}", id);
            return new NotFoundException("Отзыв с id " + id + " не найден");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь не найден: ID={}", userId);
            return new NotFoundException("Пользователь с id " + userId + " не найден");
        });
        reviewRating.getUsersLikes().add(user);
        reviewRating.getUsersDislikes().remove(user);

        ratingRepository.save(reviewRating);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> addDislikeOnReview(Long id, Long userId) {
        log.debug("Попытка добавить дизлайк на отзыв: ID={}", id);
        ReviewRating reviewRating = ratingRepository.findByReviewId(id).orElseThrow(() -> {
            log.error("Отзыв не найден: ID={}", id);
            return new NotFoundException("Отзыв с id " + id + " не найден");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь не найден: ID={}", userId);
            return new NotFoundException("Пользователь с id " + userId + " не найден");
        });
        reviewRating.getUsersDislikes().add(user);
        reviewRating.getUsersLikes().remove(user);

        ratingRepository.save(reviewRating);

        return ResponseEntity.ok().build();
    }

    @Transactional
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
    public ResponseEntity<Void> deleteDislikeFromReview(Long id, Long userId) {
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

        if (!reviewRating.getUsersDislikes().remove(user)) {
            log.warn("Пользователь {} не ставил дилзайк отзыву {}", userId, id);
            throw new ValidationException("Пользователь не ставил дилзайк этому отзыву");
        }

        ratingRepository.save(reviewRating);
        log.info("Дизлайк пользователя {} удален с отзыва {}", userId, id);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Set<ReviewResponseDto>> getReviewsToFilm(Long filmId, int count) {
        log.debug("Попытка получить все отзывы на фильм с ID={}", filmId);

        if (filmId == null) {
            Set<Review> reviews = new HashSet<>(reviewRepository.findAll());
            Set<ReviewResponseDto> collect = reviews.stream()
                    .map(reviewMapper::toReviewDto)
                    .collect(Collectors.toSet());

            log.debug("Возвращено {} отзывов", collect.size());
            return ResponseEntity.ok(collect);
        }

        Set<Review> reviews = reviewRepository.findAllByFilmId(filmId).orElse(new HashSet<>())
                .stream().limit(count).collect(Collectors.toSet());
        Set<ReviewResponseDto> collect = reviews.stream()
                .map(reviewMapper::toReviewDto)
                .collect(Collectors.toSet());

        log.debug("Возвращено {} отзывов", collect.size());
        return ResponseEntity.ok(collect);
    }

    @Transactional
    public ResponseEntity<ReviewResponseDto> updateReview(ChangeReviewDto updatedReview) {
        log.debug("Попытка обновить отзыв на фильм ID={}", updatedReview.getFilmId());

        Long reviewId = updatedReview.getReviewId();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID %s не найден".formatted(reviewId)));

        if (updatedReview.getContent() != null && !updatedReview.getContent().equals(review.getContent())) {
            log.debug("Обновление текста в отзыве");
            review.setContent(updatedReview.getContent());
        }

        if (updatedReview.getIsPositive() != null && !updatedReview.getIsPositive().equals(review.getIsPositive())) {
            log.debug("Обновление оценки отзыва");
            review.setIsPositive(updatedReview.getIsPositive());
        }

        reviewRepository.save(review);
        eventService.createEvent(review.getUser().getId(), EventType.REVIEW, EventOperation.UPDATE, review.getId());

        log.info("Отзыв с ID %s успешно обновлен".formatted(reviewId));
        return ResponseEntity.ok().body(reviewMapper.toReviewDto(review));
    }

    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        log.debug("Запрос всех отзывов");
        List<ReviewResponseDto> reviews = reviewRepository.findAll().stream()
                .map(reviewMapper::toReviewDto)
                .toList();

        log.info("Возвращено {} фильмов", reviews.size());
        return ResponseEntity.ok(reviews);
    }

    @Transactional
    public void deleteAllReviews() {
        log.warn("Удаление всех отзывов");
        reviewRepository.deleteAll();
        log.info("Все отзывы удалены");
    }
}
