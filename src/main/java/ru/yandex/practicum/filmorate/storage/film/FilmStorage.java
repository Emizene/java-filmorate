package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    void deleteAll();

    void addFilm(Film film);

    void updateFilm(Film film);

    Film getFilmById(Long filmId);

    void deleteFilm(Long filmId);

}
