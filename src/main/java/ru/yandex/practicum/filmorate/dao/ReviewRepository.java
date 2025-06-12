package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndFilmId(Long userId, Long filmId);

    Optional<Review> findByUserId(Long userId);

    Optional<Set<Review>> findAllByFilmId(Long filmId);

//    @Modifying
//    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
//    void deleteByUserId(@Param("userId") Long userId);
//
//    @Modifying
//    @Query("DELETE FROM Review r WHERE r.film.id = :filmId")
//    void deleteByFilmId(@Param("filmId") Long filmId);

//    List<Review> findAllByFilmId(Long filmId);
}
