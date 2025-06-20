package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DataGatewayService;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "spring", uses = {
        GenreMapper.class,
        MpaMapper.class,
        DirectorMapper.class,
        ReviewMapper.class,
        DataGatewayService.class
})
public abstract class FilmMapper {

    @Autowired
    protected DataGatewayService dataGatewayService;

    @Autowired
    protected ReviewMapper reviewMapper;  // Для явного вызова в кастомных методах

    @Mapping(source = "userWithLikesId", target = "usersWithLikes", qualifiedByName = "mapUsersWithLikes")
    @Mapping(source = "mpa", target = "mpaRating", qualifiedByName = "mapMpaToEntity")
    @Mapping(source = "genres", target = "genres", qualifiedByName = "mapGenre")
    @Mapping(source = "directors", target = "directors", qualifiedByName = "mapDirector")
    public abstract Film toEntity(ChangeFilmDto changeFilmDto);


    @Mapping(target = "likes", expression = "java(film.getUsersWithLikes().size())")
    @Mapping(source = "mpaRating", target = "mpa")
    @Mapping(source = "genres", target = "genres")
    @Mapping(source = "directors", target = "directors")
    @Mapping(source = "film", target = "reviews", qualifiedByName = "mapReview")
    public abstract FilmResponseDto toFilmDto(Film film);

    public abstract List<FilmResponseDto> toFilmDtoList(List<Film> film);

    @Named("mapMpaToEntity")
    Mpa mapMpaToEntity(@Nullable MpaDto mpa) {
        if (mpa == null) {
            return null;
        }
        return dataGatewayService.findMpaOrNull(mpa.getId());
    }

    @Named("mapGenre")
    Genre mapGenre(GenreDto genres) {
        return dataGatewayService.findGenreOrNull(genres.getId());
    }

    @Named("mapDirector")
    Director mapDirector(DirectorDto director) {
        if (director == null || director.getId() == null) {
            return null;
        }
        return dataGatewayService.findDirectorOrThrow(director.getId());
    }

    @Named("mapUsersWithLikes")
    protected List<User> mapUsersWithLikes(List<Long> userIds) {
        return dataGatewayService.findUsersWithLikes(userIds);
    }

    @Named("mapReview")
    Set<ReviewResponseDto> mapReview(Film film) {
        return dataGatewayService.findReviewsForFilm(film.getId())
                .stream()
                .map(reviewMapper::toReviewDto)
                .collect(Collectors.toSet());
    }
}