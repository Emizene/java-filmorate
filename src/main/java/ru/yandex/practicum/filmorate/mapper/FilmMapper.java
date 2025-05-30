package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GenreMapper.class, MpaMapper.class})
public abstract class FilmMapper {
    @Autowired
    MpaRepository mpaRepository;
    @Autowired
    GenreRepository genreRepository;

    @Mapping(source = "mpa", target = "mpaRating", qualifiedByName = "mapMpaToEntity")
//    @Mapping(source = "genreId", target = "genres", qualifiedByName = "mapGenre")
    public abstract Film toEntity(ChangeFilmDto changeFilmDto);

    @Mapping(target = "likes", expression = "java(film.getUsersWithLikes().size())")
    @Mapping(source = "mpaRating", target = "mpa")
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
    Genre mapGenre(Long genreId) {
        return genreRepository.findById(genreId).orElse(null);
    }

}