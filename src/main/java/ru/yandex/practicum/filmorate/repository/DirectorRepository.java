package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    boolean existsByName(String name);

    List<Director> findByNameContainingIgnoreCase(String query);
}
