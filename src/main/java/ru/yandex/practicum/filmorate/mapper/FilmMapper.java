package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GenreMapper.class, MpaMapper.class})
public interface FilmMapper {
    Film toEntity(ChangeFilmDto changeFilmDto);
    @Mapping(target = "likes", expression = "java(film.getUsersWithLikes().size())")
    FilmResponseDto toFilmDto(Film film);
    List<FilmResponseDto> toFilmDtoList(List<Film> film);
}