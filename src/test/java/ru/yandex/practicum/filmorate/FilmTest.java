package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.ChangeUserDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmTest extends FilmorateApplicationTests {

    @BeforeEach
    void beforeEach() {
        filmService.deleteAllFilms();
    }

    @Test
    public void testSuccessGetFilm() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}]"));
    }

    @Test
    public void testSuccessAddFilm() throws Exception {
        assertEquals(0, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"));
    }

    @Test
    public void testSuccessUpdateFilm() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"New name 1\",\"description\":\"New description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"New name 1\",\"description\":\"New description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"));
    }

    @Test
    public void testSuccessAddLike() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        filmService.addLike(1L, 1L);

        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"));
    }

    @Test
    public void testSuccessDeleteLike() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        filmService.addLike(1L, 1L);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"));
        filmService.deleteLike(1L, 1L);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"));
    }

    @Test
    public void testSuccessGerPopularFilms() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        ChangeFilmDto film2 = new ChangeFilmDto("Name 2", "Description 2",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        filmService.addFilm(film2);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);

        filmService.addLike(1L, 1L);

        mockMvc.perform(get("/films/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1},{\"id\":2,\"name\":\"Name 2\",\"description\":\"Description 2\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}]"));

    }

    @Test
    public void testSuccessGetMpa() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);

        assertEquals("G", film1.getMpa().getName());
    }

    @Test
    public void testSuccessGetGenre() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);

        GenreDto genre = film1.getGenres().getFirst();
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testInvalidName() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\",\"duration\": 120}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInvalidDescriptionSize() throws Exception {
        String exactly201Chars = "D".repeat(201);
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"" + exactly201Chars + "\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidReleaseDate() {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(1000, 7, 27), 120,
                new MpaDto(1L, null), List.of(new GenreDto(1L, null)));

        Exception exception = assertThrows(ValidationException.class,
                () -> filmService.addFilm(film1));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void testInvalidBirthday() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\",\"duration\": -15}"))
                .andExpect(status().isNotFound());
    }

}
