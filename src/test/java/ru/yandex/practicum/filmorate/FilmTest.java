package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilmTest extends FilmorateApplicationTests {

    @BeforeEach
    void beforeEach() {
        filmService.deleteAllFilms();
    }

    @Test
    public void testSuccessGetFilm() throws Exception {
        Film film1 = new Film("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120);
        filmService.addFilm(film1);
        mockMvc.perform(get("/film"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1,"name": "Name 1","description": "Description 1",
                        "releaseDate": "2000-07-27","duration": 120}]
                        """));
    }

    @Test
    public void testSuccessAddFilm() throws Exception {
        assertEquals(0, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
        mockMvc.perform(post("/film")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Новое Название 1","description":"Новое Описание 1",
                                "releaseDate":"2000-07-27","duration":120}
                                """))
                .andExpect(status().isCreated());
        assertEquals(1, filmService.getAllFilms().getBody().size());
    }

    @Test
    public void testSuccessUpdateFilm() throws Exception {
        Film film1 = new Film("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120);
        filmService.addFilm(film1);
        mockMvc.perform(patch("/film")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id": 1,"name": "new name 1","description": "new description 1",
                                "releaseDate": "2000-07-27","duration": 120}
                                """))
                .andExpect(status().isOk());
        Film updateFilm = Objects.requireNonNull(filmService.getAllFilms().getBody()).getFirst();
        assertEquals("new name 1", updateFilm.getName());
        assertEquals("new description 1", updateFilm.getDescription());
        assertEquals(1, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
    }

    @Test
    public void testInvalidName() {
        Film film1 = new Film("", "Description 1",
                LocalDate.of(2000, 7, 27), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmService.validateFilm(film1));

        assertEquals("Это поле обязательно для заполнения", exception.getMessage());
    }

    @Test
    public void testInvalidDescriptionSize() {
        String exactly201Chars = "D".repeat(201);
        Film film1 = new Film("Name 1", exactly201Chars,
                LocalDate.of(2000, 7, 27), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmService.validateFilm(film1));

        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testInvalidReleaseDate() {
        Film film1 = new Film("Name 1", "Description 1",
                LocalDate.of(1000, 7, 27), 120);

        Exception exception = assertThrows(ValidationException.class,
                () -> filmService.validateFilm(film1));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void testInvalidBirthday() {
        Film film1 = new Film("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), -15);

        Exception exception = assertThrows(ValidationException.class,
                () -> filmService.validateFilm(film1));

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

}
