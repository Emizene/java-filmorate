package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class FilmResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private MpaDto mpa;
    private List<DirectorDto> directors;
    private List<GenreDto> genres;
    private int likes;
    private Set<ReviewResponseDto> reviews;
}
