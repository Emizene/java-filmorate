package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserTest extends FilmorateApplicationTests {

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userService.deleteAllUsers();
    }

    @Test
    void testSuccessCreateUser() throws Exception {
        assertEquals(0, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
                .andExpect(status().isCreated());
        assertEquals(1, userService.getAllUsers().getBody().size());
    }

    @Test
    void testSuccessUpdateUser() throws Exception {
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(put("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\": 1,\"email\": \"updateemail1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"email\": \"updateemail1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"));
    }

    @Test
    void testSuccessGetUser() throws Exception {
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"email\": \"email1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}]"));
    }

    @Test
    void testSuccessAddAndGetFriend() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        User user2 = new User("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        userService.addFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 2,\"email\": \"email2@yandex.ru\",\"login\": \"user2\",\"name\": \"Ян2\",\"birthday\": \"1996-12-05\"}]"));
    }

    @Test
    void testSuccessDeleteFriend() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        User user2 = new User("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        userService.addFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 2,\"email\": \"email2@yandex.ru\",\"login\": \"user2\",\"name\": \"Ян2\",\"birthday\": \"1996-12-05\"}]"));

        userService.deleteFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testSuccessGetCommonFriends() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        User user2 = new User("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        User user3 = new User("email3@yandex.ru", "user3", "Ян3",
                LocalDate.of(1996, 12, 5));
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);
        User saved3 = userRepository.save(user3);

        userService.addFriend(saved1.getId(), saved2.getId());
        userService.addFriend(saved3.getId(), saved2.getId());

        userService.getCommonFriends(saved1.getId(), saved3.getId());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 2,\"email\": \"email2@yandex.ru\",\"login\": \"user2\",\"name\": \"Ян2\",\"birthday\": \"1996-12-05\"}]"));
    }

    @Test
    void testInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidEmptyEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"login 1\",\"name\": \"name\",\"birthday\": \"2000-1-11\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidEmptyLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2026-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkMethodGetRecommendations() throws Exception {
        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);
        ChangeFilmDto film2 = new ChangeFilmDto("Name 2", "Description 2",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film2);
        ChangeFilmDto film3 = new ChangeFilmDto("Name 3", "Description 3",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), List.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film3);
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        ChangeUserDto user2 = new ChangeUserDto("email2@yandex.ru", "user2", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user2);
        ChangeUserDto user3 = new ChangeUserDto("email3@yandex.ru", "user3", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user3);
        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        filmService.addLike(3L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(3L, 2L);

        mockMvc.perform(get("/users/1/recommendations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(get("/users/2/recommendations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Name 2"));

        mockMvc.perform(get("/users/3/recommendations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @Transactional
    void testDeleteNonExistingUser() {
        long nonExistingId = 999L;

        assertThrows(NotFoundException.class, () -> userService.deleteUser(nonExistingId));

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(nonExistingId));
        assertTrue(exception.getMessage().contains("Пользователь с ID 999 не найден"));
    }

    @Test
    @Transactional
    void testDeleteUser() throws Exception {
        // Создание пользователей
        User friend1 = userRepository.save(new User("friend1@mail.com", "friend1",
                "Friend One", LocalDate.of(1990, 1, 1)));
        User friend2 = userRepository.save(new User("friend2@mail.com", "friend2",
                "Friend Two", LocalDate.of(1992, 2, 2)));
        // Пользователь, который будет удален
        User mainUser = userRepository.save(new User("main@mail.com", "main_user",
                "Main User", LocalDate.of(1985, 5, 5)));

        // Инициализация и установка связей
        mainUser.setFriends(new HashSet<>());
        mainUser.getFriends().add(friend1);
        mainUser.getFriends().add(friend2);
        friend1.setFriends(new HashSet<>());
        friend1.getFriends().add(mainUser);

        // Сохраняем пользователей и связи
        userRepository.saveAll(List.of(mainUser, friend1, friend2));

        Long userId = mainUser.getId();

        // Проверка перед удалением
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk());

        // Удаляем пользователя
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNoContent());

        // Проверяем, что пользователь удален
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());

        // Проверки дружеских связей - односторонняя модель
        User reloadedFriend1 = userRepository.findById(friend1.getId()).orElseThrow();
        User reloadedFriend2 = userRepository.findById(friend2.getId()).orElseThrow();

        // Проверяем связи используя id
        assertFalse(reloadedFriend1.getFriends().stream()
                        .anyMatch(u -> u.getId().equals(userId)),
                "Друг 1 все еще имеет связь");

        assertFalse(reloadedFriend2.getFriends().stream()
                        .anyMatch(u -> u.getId().equals(userId)),
                "Друг 2 все еще имеет связь");
    }

    @Test
    void checkMethodGetEventFeed() throws Exception {
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "name1",
                LocalDate.of(2000, 1, 1));
        ChangeUserDto user2 = new ChangeUserDto("email2@yandex.ru", "user2", "name2",
                LocalDate.of(2000, 2, 2));
        ChangeFilmDto film = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120L,
                new MpaDto(1L, "G"), List.of(new DirectorDto(1L, "Гайдай")), List.of(new GenreDto(1L, "Комедия")));

        userService.createUser(user1);
        userService.createUser(user2);
        filmService.addFilm(film);
        userService.addFriend(1L, 2L);
        filmService.addLike(1L, 1L);
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        userService.deleteFriend(1L, 2L);
        filmService.deleteLike(1L, 1L);
        reviewService.deleteReview(1L);

        mockMvc.perform(get("/users/1/feed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[1].eventType").value("LIKE"))
                .andExpect(jsonPath("$[1].operation").value("ADD"))
                .andExpect(jsonPath("$[2].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[2].operation").value("ADD"))
                .andExpect(jsonPath("$[3].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[3].operation").value("REMOVE"))
                .andExpect(jsonPath("$[4].eventType").value("LIKE"))
                .andExpect(jsonPath("$[4].operation").value("REMOVE"))
                .andExpect(jsonPath("$[5].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[5].operation").value("REMOVE"));
    }
}
