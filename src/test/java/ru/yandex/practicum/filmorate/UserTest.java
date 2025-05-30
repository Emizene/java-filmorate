//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class UserTest extends FilmorateApplicationTests {
//
//    @BeforeEach
//    void beforeEach() {
//        userService.deleteAllUsers();
//    }
//
//    @Test
//    public void testSuccessGetUser() throws Exception {
//        User user1 = new User("email1@yandex.ru", "user1", "Ян",
//                LocalDate.of(1996, 12, 5));
//        userService.createUser(user1);
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("""
//                        [{"id": 1,"email": "email1@yandex.ru","login": "user1",
//                        		"name": "Ян","birthday": "1996-12-05"}]
//                        """));
//    }
//
//    @Test
//    public void testSuccessCreateUser() throws Exception {
//        assertEquals(0, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
//                .andExpect(status().isCreated());
//        assertEquals(1, userService.getAllUsers().getBody().size());
//    }
//
//    @Test
//    public void testSuccessUpdateUser() throws Exception {
//        User user1 = new User("email1@yandex.ru", "user1", "Ян",
//                LocalDate.of(1996, 12, 5));
//        userService.createUser(user1);
//        mockMvc.perform(put("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content(" {\"id\": 1,\"email\": \"updateemail1@yandex.ru\",\"login\": \"user1\",\"name\": \"Ян\",\"birthday\": \"1996-12-05\"}"))
//                .andExpect(status().isOk());
//        User updateUser = Objects.requireNonNull(userService.getAllUsers().getBody()).getFirst();
//        assertEquals("updateemail1@yandex.ru", updateUser.getEmail());
//        assertEquals(1, Objects.requireNonNull(userService.getAllUsers().getBody()).size());
//    }
//
//    @Test
//    public void testInvalidEmail() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"email\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testInvalidEmptyEmail() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"\",\"login\": \"login1\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testInvalidLogin() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"email1@yandex.ru\",\"login\": \"login 1\",\"name\": \"name\",\"birthday\": \"2000-1-11\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testInvalidEmptyLogin() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2000-1-16\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testInvalidBirthday() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(APPLICATION_JSON)
//                        .content("{\"email\": \"email\",\"login\": \"\",\"name\": \"name\",\"birthday\": \"2026-1-16\"}"))
//                .andExpect(status().isBadRequest());
//    }
//}
//
