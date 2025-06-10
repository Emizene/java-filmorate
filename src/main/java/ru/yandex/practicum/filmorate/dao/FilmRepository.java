package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    /**
     * Формируем таблицу max_common_id, в которой находим user_id пользователя с максимальным совпадением по лайкам.
     * В первом условии получаем список фильмов, которые лайкнул найденный для рекомендации пользователь.
     * Во втором условии исключаем из полученного списка фильмов те, которые лайкнул сам пользователь.
     * @param userId идентификатор обратившегося пользователя, для которого формируется список рекомендаций.
     * @return список рекомендованных фильмов - тех, которые еще лайкнул обратившийся пользователь.
     */
    @Query(value = """
            SELECT f.*
            FROM films f
            JOIN likes l ON f.id = l.film_id
            WHERE l.user_id IN (
                SELECT max_common_id.user_id
                FROM (
                    SELECT l2.user_id, COUNT(*) AS count_common_likes
                    FROM likes l1
                    JOIN likes l2 ON l1.film_id = l2.film_id
                    WHERE l1.user_id = :userId AND l2.user_id != :userId
                    GROUP BY l2.user_id
                    ORDER BY count_common_likes DESC
                    LIMIT 1
                ) AS max_common_id
            )
            AND f.id NOT IN (
                SELECT l3.film_id
                FROM likes l3
                WHERE l3.user_id = :userId
            )
            """, nativeQuery = true)
    List<Film> findRecommendations(@Param("userId") Long userId);
}