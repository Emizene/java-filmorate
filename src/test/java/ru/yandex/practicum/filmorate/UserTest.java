package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTest extends FilmorateApplicationTests {

    @BeforeEach
    void beforeEach() {
        userService.deleteAllUsers();
    }

    @Test
    public void testSuccessGetUser() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                        	{
                        		"id": 1,
                        		"email": "email1@yandex.ru",
                        		"login": "user1",
                        		"name": "Ян",
                        		"birthday": "1996-12-05"
                        	}
                        ]
                        """));
    }

    @Test
    public void testSuccessCreateUser() throws Exception {
        assertEquals(0, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
        mockMvc.perform(post("/user")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                 	{
                                		"email": "email1@yandex.ru",
                                 		"login": "user1",
                                 		"name": "Ян",
                                 		"birthday": "1996-12-05"
                                 	}
                                """))
                .andExpect(status().isCreated());
        assertEquals(1, userService.getAllUsers().getBody().size());
    }

    @Test
    public void testSuccessUpdateUser() throws Exception {
        User user1 = new User("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);
        mockMvc.perform(patch("/user")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                	"id": 1,
                                	"email": "updateemail1@yandex.ru",
                                	"login": "user1",
                                	"name": "Ян",
                                	"birthday": "1996-12-05"
                                }
                                """))
                .andExpect(status().isOk());
        User updateUser = Objects.requireNonNull(userService.getAllUsers().getBody()).getFirst();
        assertEquals("updateemail1@yandex.ru", updateUser.getEmail());
        assertEquals(1, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
    }

    @Test
    public void testInvalidEmail() {
        User user1 = new User("email", "login1", "name",
                LocalDate.of(2000, 1, 16));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.validateUser(user1));

        assertEquals("Email должен содержать @", exception.getMessage());
    }

    @Test
    public void testInvalidEmptyEmail() {
        User user1 = new User("", "login1", "name",
                LocalDate.of(2000, 1, 16));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.validateUser(user1));

        assertEquals("Это поле обязательно для заполнения", exception.getMessage());
    }

    @Test
    public void testInvalidLogin() {
        User user1 = new User("email1@yandex.ru", "login 1", "name",
                LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.validateUser(user1));

        assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }

    @Test
    public void testInvalidEmptyLogin() {
        User user1 = new User("email1@yandex.ru", "", "name",
                LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.validateUser(user1));

        assertEquals("Это поле обязательно для заполнения", exception.getMessage());
    }

    @Test
    public void testInvalidBirthday() {
        User user1 = new User("email1@yandex.ru", "login1", "name",
                LocalDate.of(2026, 1, 16));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.validateUser(user1));

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}

