package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewTest extends FilmorateApplicationTests {

    @Autowired
    MpaRepository mpaRepository;

    @Autowired
    GenreRepository genreRepository;

    @BeforeEach
    void beforeEach() {
        mpaRepository.save(new Mpa(1L, "G"));
        genreRepository.save(new Genre(1L, "Комедия"));

        ChangeFilmDto film1 = new ChangeFilmDto("Name 1", "Description 1",
                LocalDate.of(2000, 7, 27), 120,
                new MpaDto(1L, "G"), Set.of(new GenreDto(1L, "Комедия")));
        filmService.addFilm(film1);

        ChangeUserDto user1 = new ChangeUserDto("email1@yandex.ru", "user1", "Ян",
                LocalDate.of(1996, 12, 5));
        userService.createUser(user1);

        reviewService.deleteAllReviews();
    }
    @Test
    void testSuccessAddReview() throws Exception {
        assertEquals(0, Objects.requireNonNull(reviewService.getAllReviews().getBody()).size());
        mockMvc.perform(post("/reviews")
                        .contentType(APPLICATION_JSON)
                        .content("{\"content\":\"This film is soo bad.\",\"isPositive\":false,\"userId\": 1,\"filmId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is soo bad.\",\"isPositive\":false,\"useful\":0,\"userId\":1,\"filmId\":1}"));
    }

    @Test
    void testSuccessGetReviewById() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);
        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":0,\"userId\":1,\"filmId\":1}"));
    }

    @Test
    void testSuccessDeleteReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":0,\"userId\":1,\"filmId\":1}"));

        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk());

        assertEquals(0, Objects.requireNonNull(reviewService.getAllReviews().getBody()).size());
    }

    @Test
    void testSuccessGetReviewsToFilm() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);
        mockMvc.perform(get("/reviews").param("filmId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":0,\"userId\":1,\"filmId\":1}]"));
    }

    @Test
    void testSuccessAddLikeOnReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(put("/reviews/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":1,\"userId\":1,\"filmId\":1}"));

    }

    @Test
    void testSuccessAddDislikeOnReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is so bad.", false, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(put("/reviews/1/dislike/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is so bad.\",\"isPositive\":false,\"useful\":-1,\"userId\":1,\"filmId\":1}"));

    }

    @Test
    void testSuccessDeleteLikeFromReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(put("/reviews/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":1,\"userId\":1,\"filmId\":1}"));

        mockMvc.perform(delete("/reviews/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":0,\"userId\":1,\"filmId\":1}"));

    }

    @Test
    void testSuccessDeleteDislikeFromReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(put("/reviews/1/dislike/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":-1,\"userId\":1,\"filmId\":1}"));

        mockMvc.perform(delete("/reviews/1/dislike/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is good.\",\"isPositive\":true,\"useful\":0,\"userId\":1,\"filmId\":1}"));

    }

    @Test
    void testSuccessUpdateReview() throws Exception {
        ChangeReviewDto review = new ChangeReviewDto("This film is good.", true, 1L, 1L);
        reviewService.addReview(review);

        mockMvc.perform(put("/reviews")
                .contentType(APPLICATION_JSON)
                .content("{\"reviewId\":1,\"content\":\"This film is bad.\",\"isPositive\":false,\"useful\":0,\"userId\":1,\"filmId\":1}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"reviewId\":1,\"content\":\"This film is bad.\",\"isPositive\":false,\"useful\":0,\"userId\":1,\"filmId\":1}"));
    }

}