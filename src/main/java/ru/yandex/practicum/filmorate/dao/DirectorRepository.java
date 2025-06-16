package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    boolean existsByName(String name);
}

