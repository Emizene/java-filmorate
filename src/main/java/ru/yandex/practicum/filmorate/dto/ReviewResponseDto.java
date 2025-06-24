package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewResponseDto {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Integer useful;
    private Long userId;
    private Long filmId;

    public ReviewResponseDto(Long reviewId, String content, Boolean isPositive, Integer useful, Long userId, Long filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.useful = useful;
        this.userId = userId;
        this.filmId = filmId;
    }
}