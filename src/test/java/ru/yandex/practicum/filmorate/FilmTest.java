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
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1,"name": "Name 1","description": "Description 1",
                        "releaseDate": "2000-07-27","duration": 120}]
                        """));
    }

    @Test
    public void testSuccessAddFilm() throws Exception {
        assertEquals(0, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Новое Название 1\",\"description\":\"Новое Описание 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isCreated());
        assertEquals(1, filmService.getAllFilms().getBody().size());
    }

    @Test
    public void testSuccessUpdateFilm() throws Exception {
        Film film1 = new Film("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120);
        filmService.addFilm(film1);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\": 1,\"name\": \"new name 1\",\"description\": \"new description 1\",\"releaseDate\": \"2000-07-27\",\"duration\": 120}"))
                .andExpect(status().isOk());
        Film updateFilm = Objects.requireNonNull(filmService.getAllFilms().getBody()).getFirst();
        assertEquals("new name 1", updateFilm.getName());
        assertEquals("new description 1", updateFilm.getDescription());
        assertEquals(1, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
    }

    @Test
    public void testInvalidName() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\",\"duration\": 120}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDescriptionSize() throws Exception {
        String exactly201Chars = "D".repeat(201);
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":" + exactly201Chars + ",\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isBadRequest());
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
    public void testInvalidBirthday() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\",\"duration\": -15}"))
                .andExpect(status().isBadRequest());
    }

}
