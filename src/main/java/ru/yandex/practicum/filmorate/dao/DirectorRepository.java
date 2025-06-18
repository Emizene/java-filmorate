package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorRepository {
    // Поиск режиссёров по имени
    List<Director> findByNameStartingWithIgnoreCase(String query);
}