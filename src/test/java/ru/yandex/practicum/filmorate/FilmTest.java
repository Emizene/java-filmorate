package ru.yandex.practicum.filmorate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmTest extends FilmorateApplicationTests {

    @Autowired
    MpaRepository mpaRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        filmService.deleteAllFilms();
    }

    @Test
    void testSuccessGetFilm() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));
        directorRepository.save(new Director(1L, "Гайдай"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}]"));
    }

    @Test
    void testSuccessAddFilm() throws Exception {
        assertEquals(0, Objects.requireNonNull(filmService.getAllFilms().getBody()).size());
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120}"));
    }

    @Test
    void testSuccessUpdateFilm() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));
        directorRepository.save(new Director(1L, "Гайдай"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));

        filmService.addFilm(film1);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"New name 1\",\"description\":\"New description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"New name 1\",\"description\":\"New description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"));
    }

    @Test
    void testSuccessAddLike() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));
        directorRepository.save(new Director(1L, "Гайдай"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        filmService.addLike(1L, 1L);

        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"));
    }

    @Test
    void testSuccessDeleteLike() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));
        directorRepository.save(new Director(1L, "Гайдай"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));

        filmService.addFilm(film1);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        filmService.addLike(1L, 1L);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1}"));
        filmService.deleteLike(1L, 1L);
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}"));
    }

    @Test
    void testSuccessGetPopularFilms() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));
        directorRepository.save(new Director(1L, "Гайдай"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));
        ChangeFilmDto film2 = new ChangeFilmDto("Name 2", "Description 2",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));

        filmService.addFilm(film1);
        filmService.addFilm(film2);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);

        filmService.addLike(1L, 1L);

        mockMvc.perform(get("/films/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"directors\":[{\"id\":1,\"name\":\"Гайдай\"}],\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":1},{\"id\":2,\"name\":\"Name 2\",\"description\":\"Description 2\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":0}]"));

    }

    @Test
    void testSuccessGetMpa() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));

        filmService.addFilm(film1);

        assertEquals("G", film1.getMpa().getName());
    }

    @Test
    void testSuccessGetGenre() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), Set.of(new GenreDto(1L, "Комедия")));

        filmService.addFilm(film1);

        GenreDto genre = film1.getGenres().iterator().next();
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testSuccessGetDirector() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай"), new DirectorDto(2L,
                "Гай Ричи")), Set.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);

        List<DirectorDto> directors = film1.getDirectors();
        assertEquals(2, directors.size());

        for (DirectorDto director : directors) {
            if (director.getId().equals(1L)) {
                assertEquals("Гайдай", director.getName());
            } else if (director.getId().equals(2L)) {
                assertEquals("Гай Ричи", director.getName());
            } else {
                fail("Режиссер с таким id не найден: " + director.getId());
            }
        }
    }

    @Test
    public void testInvalidName() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\"," +
                                "\"duration\": 120}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidDescriptionSize() throws Exception {
        String exactly201Chars = "D".repeat(201);
        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Name 1\",\"description\":\"" + exactly201Chars + "\"," +
                                "\"releaseDate\":\"2000-07-27\",\"duration\":120}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidReleaseDate() {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(1000, 7, 27), 120L,
                new MpaDto(1L, null), List.of(new DirectorDto(1L, null)), Set.of(new GenreDto(1L,
                null)));

        Exception exception = assertThrows(ValidationException.class,
                () -> filmService.addFilm(film1));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void testInvalidBirthday() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"\",\"description\": \"Description 1\",\"releaseDate\": \"2000-07-27\"," +
                                "\"duration\": -15}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindFilmsByDirectorSorted() {
        Director director1 = new Director();
        director1.setId(1L);
        director1.setName("Test Director");
        directorRepository.save(director1);
        Film film1 = new Film();
        User user1 = new User();
        user1.setLogin("example-user");
        user1.setEmail("user@example.com");
        User user2 = new User();
        user2.setLogin("example2-user");
        user2.setEmail("user2@example.com");
        userRepository.save(user1);
        userRepository.save(user2);
        film1.setId(1L);
        film1.setName("Фильм 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(30L);
        film1.setDirectors(List.of(director1));
        film1.setUsersWithLikes(List.of(user1));
        filmRepository.save(film1);

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("Фильм 2");
        film2.setReleaseDate(LocalDate.of(2023, 2, 1));
        film2.setDuration(40L);
        film2.setDirectors(List.of(director1));
        film2.setUsersWithLikes(Arrays.asList(user1, user2));
        filmRepository.save(film2);

        ResponseEntity<List<FilmResponseDto>> sortedFilmsYear = filmService.findFilmsByDirectorSorted(1L,
                "year");
        ResponseEntity<List<FilmResponseDto>> sortedFilmsLikes = filmService.findFilmsByDirectorSorted(1L,
                "likes");

        assertEquals(HttpStatus.OK, sortedFilmsYear.getStatusCode());
        assertEquals(2, sortedFilmsYear.getBody().size());
        assertEquals(1L, sortedFilmsYear.getBody().get(0).getId());
        assertEquals(2L, sortedFilmsYear.getBody().get(1).getId());

        assertEquals(HttpStatus.OK, sortedFilmsLikes.getStatusCode());
        assertEquals(2, sortedFilmsLikes.getBody().size());
        assertEquals(2L, sortedFilmsLikes.getBody().get(0).getId());
        assertEquals(1L, sortedFilmsLikes.getBody().get(1).getId());
    }

    @Test
    public void testDeleteDirector() throws Exception {
        Director director = new Director();
        director.setId(1L);
        director.setName("Test Director");
        directorRepository.save(director);

        MockHttpServletRequestBuilder request = delete("/directors/1");
        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();
        assertFalse(directorRepository.existsById(1L));
    }

    @Test
    public void testDeleteFilm() throws Exception {
        Mpa mpa = mpaRepository.save(new Mpa(1L, "PG-13"));
        Genre genre = genreRepository.save(new Genre(1L, "Комедия"));

        ChangeFilmDto filmDto = new ChangeFilmDto(
                "Фильм для удаления",
                "Описание удаляемого фильма",
                LocalDate.of(2020, 1, 1),
                120L,
                new MpaDto(mpa.getId(), mpa.getName()),
                Set.of(new GenreDto(genre.getId(), genre.getName()))
        );

        ResponseEntity<FilmResponseDto> response = filmService.addFilm(filmDto);
        FilmResponseDto createdFilm = response.getBody();
        Long filmId = Objects.requireNonNull(createdFilm).getId();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        mockMvc.perform(get("/films/" + filmId))
                .andExpect(status().isOk());

        // Проверяем, что фильм есть в общем списке
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/films/" + filmId))
                .andExpect(status().isNoContent());

        // Проверяем, что фильм исчез из общего списка
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Пытаемся получить удаленный фильм
        mockMvc.perform(get("/films/" + filmId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistingFilm() {
        assertThrows(NotFoundException.class, () -> filmService.deleteFilm(999L));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> filmService.deleteFilm(999L));

        assertTrue(exception.getMessage().contains("Фильм с ID 999 не найден"));
    }

    @Test
    public void testSuccessGetCommonFilms() throws Exception {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), Set.of(new GenreDto(1L, "Комедия")));
        ChangeFilmDto film2 = new ChangeFilmDto("Name 2", "Description 2",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), Set.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        filmService.addFilm(film2);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        ChangeUserDto user2 = new ChangeUserDto("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        userService.createUser(user2);

        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        filmService.addLike(1L, 2L);

        mockMvc.perform(get("/films/common")
                        .param("userId", "1")
                        .param("friendId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Name 1\",\"description\":\"Description 1\",\"releaseDate\":\"2000-07-27\",\"duration\":120,\"mpa\":{\"id\":1,\"name\":\"G\"},\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"likes\":2,\"reviews\":[]}]"));
    }
}
