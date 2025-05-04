package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final List<Film> films = new ArrayList<>();

    @Override
    public List<Film> getAllFilms() {
        return Collections.unmodifiableList(films);
    }

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public void addFilm(Film film) {
        films.add(film);
    }

    @Override
    public void updateFilm(Film film) {
        Optional<Film> filmOptional = films.stream()
                .filter(film1 -> film1.getId().equals(film.getId()))
                .findFirst();
        if (filmOptional.isPresent()) {
            films.remove(filmOptional.get());
            films.add(film);
        }
    }

    @Override
    public Film getFilmById(Long id) {
        return films.stream()
                .filter(film -> film.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    @Override
    public void deleteFilm(Long id) {
        films.stream()
                .filter(film -> film.getId().equals(id))
                .findFirst()
                .ifPresent(films::remove);
    }

}
