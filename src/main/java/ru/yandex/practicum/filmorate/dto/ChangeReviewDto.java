package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeReviewDto {
    private Long id;
    private String review;
    private Boolean isPositive;
    private Integer useful;
    private Long userId;
    private Long filmId;
}
