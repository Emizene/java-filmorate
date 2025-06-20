package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndFilmId(Long userId, Long filmId);

    Optional<Set<Review>> findAllByFilmId(Long filmId);

    boolean existsByUserIdAndFilmId(Long userId, Long filmId);
}