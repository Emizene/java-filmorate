package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.util.Optional;

@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRating, Long> {
    Optional<ReviewRating> findByReviewId(Long reviewId);
}
