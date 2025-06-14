package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.util.Optional;

public interface ReviewRatingRepository extends JpaRepository<ReviewRating, Long> {
    Optional<ReviewRating> findByReviewId(Long reviewId);
}
