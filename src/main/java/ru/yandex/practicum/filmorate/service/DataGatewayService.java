package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataGatewayService {
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public Mpa findMpaOrNull(Long id) {
        return id != null ? mpaRepository.findById(id).orElse(null) : null;
    }

    public Genre findGenreOrNull(Long id) {
        return id != null ? genreRepository.findById(id).orElse(null) : null;
    }

    public Director findDirectorOrThrow(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + id + " не найден"));
    }

    public List<User> findUsersWithLikes(List<Long> ids) {
        return ids != null && !ids.isEmpty()
                ? userRepository.findAllById(ids)
                : new ArrayList<>();
    }

    public Set<Review> findReviewsForFilm(Long filmId) {
        return reviewRepository.findAllByFilmId(filmId).orElse(new HashSet<>());
    }
}