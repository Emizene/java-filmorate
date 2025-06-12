package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

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
        this.useful = Objects.requireNonNullElse(useful, 0);
        this.userId = userId;
        this.filmId = filmId;
    }

    //TODO убрать конструктор в аннотацию, когда добавятся лайки
}
