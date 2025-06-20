package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class ChangeFilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private MpaDto mpa;
    private List<DirectorDto> directors;
    private List<GenreDto> genres;
    private List<Long> userWithLikesId;
    private Set<ReviewResponseDto> reviews;

    public ChangeFilmDto(String name, String description, LocalDate releaseDate,
                         Long duration, MpaDto mpa, List<DirectorDto> directors, List<GenreDto> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
        this.directors = directors;
    }
}