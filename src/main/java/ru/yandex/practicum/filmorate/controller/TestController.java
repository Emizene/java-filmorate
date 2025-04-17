package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class TestController {
    private final UserService userService;
    private final FilmService filmService;

    public TestController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping("/user")
    public void fillWithUsers() {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        User user2 = new User("email2@yandex.ru", "user2", "",
                LocalDate.of(1980, 6, 13));
        userService.createUser(user2);
    }

    @GetMapping("/film")
    public void fillWithFilms() {
        Film film1 = new Film("Название 1", "Описание 1",
                LocalDate.of(2000, 7, 27), 120);
        filmService.addFilm(film1);
        Film film2 = new Film("Название 2", "Описание 2",
                LocalDate.of(2014, 2, 15), 75);
        filmService.addFilm(film2);
    }

}

