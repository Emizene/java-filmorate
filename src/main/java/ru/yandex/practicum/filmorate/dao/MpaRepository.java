package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

@Repository
public interface MpaRepository extends JpaRepository<Mpa, Long> {
}
