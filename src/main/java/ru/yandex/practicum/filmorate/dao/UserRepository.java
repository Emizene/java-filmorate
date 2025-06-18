package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.friends f WHERE f.id = :friendId")
    Set<User> findUsersWhoAddedAsFriend(@Param("friendId") Long friendId);
}
