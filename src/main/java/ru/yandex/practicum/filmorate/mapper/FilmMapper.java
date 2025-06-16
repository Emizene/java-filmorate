package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dao.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Mapper(componentModel = "spring", uses = {GenreMapper.class, MpaMapper.class, DirectorMapper.class, ReviewMapper.class})
public abstract class FilmMapper {
    @Autowired
    MpaRepository mpaRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    DirectorRepository directorRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewMapper reviewMapper;

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
    Mpa mapMpaToEntity(MpaDto mpa) {
        if (mpa == null || mpa.getId() == null) {
            return null;
        }
        return mpaRepository.findById(mpa.getId()).orElse(null);
    }

    @Named("mapGenre")
    Genre mapGenre(GenreDto genres) {
        return genreRepository.findById(genres.getId()).orElse(null);
    }

    @Named("mapDirector")
    Director mapDirector(DirectorDto director) {
        if (director == null || director.getId() == null) {
            return null;
        }

        Optional<Director> foundDirector = directorRepository.findById(director.getId());
        if (foundDirector.isPresent()) {
            return foundDirector.get();
        } else {
            log.error("Режиссёр с ID {} не найден", director.getId());
            throw new NotFoundException("Режиссёр с ID " + director.getId() + " не найден");
        }
    }
    
    @Named("mapReview")
    Set<ReviewResponseDto> mapReview(Film film) {
        Set<Review> reviews = reviewRepository.findAllByFilmId(film.getId()).orElse(new HashSet<>());
        return reviews.stream()
                .map(reviewMapper::toReviewDto)
                .collect(Collectors.toSet());
    }
}