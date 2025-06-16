package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ChangeReviewDto {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Integer useful;
    private Long userId;
    private Long filmId;

    public ChangeReviewDto(String content, Boolean isPositive, Long userId, Long filmId) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}
