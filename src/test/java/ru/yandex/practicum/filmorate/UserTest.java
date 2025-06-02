package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dto.ChangeUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserTest extends FilmorateApplicationTests {

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userService.deleteAllUsers();
    }

    @Test
    public void testSuccessCreateUser() throws Exception {
        assertEquals(0, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
                .andExpect(status().isCreated());
        assertEquals(1, userService.getAllUsers().getBody().size());
    }

    @Test
    public void testSuccessUpdateUser() throws Exception {
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(put("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\": 1,\"email\": \"updateemail1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id": 1,"email": "updateemail1@yandex.ru","login": "user1","name": "Ян","birthday": "1996-12-05"}
                        """));
    }

    @Test
    public void testSuccessGetUser() throws Exception {
        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1,"email": "email1@yandex.ru","login": "user1",
                        		"name": "Ян","birthday": "1996-12-05"}]
                        """));
    }

    @Test
    public void testSuccessAddAndGetFriend() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        User user2 = new User("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        userService.addFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 2,"email": "email2@yandex.ru","login": "user2",
                        		"name": "Ян2","birthday": "1996-12-05"}]
                        """));
    }

    @Test
    public void testSuccessDeleteFriend() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        User user2 = new User("email2@yandex.ru", "user2", "Ян2",
                LocalDate.of(1996, 12, 5));
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        userService.addFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 2,"email": "email2@yandex.ru","login": "user2",
                        		"name": "Ян2","birthday": "1996-12-05"}]
                        """));

        userService.deleteFriend(saved1.getId(), saved2.getId());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        []
                        """));
    }

    @Test
    public void testSuccessGetCommonFriends() throws Exception {
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
                .andExpect(content().json("""
                        [{"id": 2,"email": "email2@yandex.ru","login": "user2",
                        		"name": "Ян2","birthday": "1996-12-05"}]
                        """));
    }

    @Test
    public void testInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidEmptyEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"login 1\",\"name\": \"name\",\"birthday\": \"2000-1-11\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidEmptyLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2026-1-16\"}"))
                .andExpect(status().isBadRequest());
    }
}

