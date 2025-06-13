package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewResponseDto {
    private Long id;
    private String review;
    private Boolean isPositive;
    private Integer useful;
    private Long userId;
    private Long filmId;

    public ReviewResponseDto(Long id, String review, Boolean isPositive, Integer useful, Long userId, Long filmId) {
        this.id = id;
        this.review = review;
        this.isPositive = isPositive;
        this.useful = useful;
        this.userId = userId;
        this.filmId = filmId;
    }
}
