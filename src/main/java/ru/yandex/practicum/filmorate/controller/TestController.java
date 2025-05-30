package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreMapper genreMapper;

    public TestController(UserRepository userRepository, FilmRepository filmRepository, MpaRepository mpaRepository, GenreMapper genreMapper) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.mpaRepository = mpaRepository;
        this.genreMapper = genreMapper;
    }

    @GetMapping("/user")
    public void fillWithUsers() {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userRepository.save(user1);
        User user2 = new User("email2@yandex.ru", "user2", "",
                LocalDate.of(1980, 6, 13));
        userRepository.save(user2);
    }

    @GetMapping("/film")
    public List<Film> fillWithFilms() {
        Film film1 = new Film("Название 1", "Описание 1",
                LocalDate.of(2000, 7, 27), 120);
        film1.setMpaRating(mpaRepository.findById(1L).orElse(null));
        filmRepository.save(film1);
        Film film2 = new Film("Название 2", "Описание 2",
                LocalDate.of(2014, 2, 15), 75);
        filmRepository.save(film2);
        return filmRepository.findAll();
    }

    @GetMapping("/genres")
    public GenreDto fillWithGenres() {
        return genreMapper.toGenreDto(new Genre(1L,"1"));
    }
}

