package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    protected UserService userService;

    @Autowired
    protected FilmService filmService;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected MockMvc mockMvc;

}
