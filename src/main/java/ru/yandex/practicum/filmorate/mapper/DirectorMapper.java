package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {FilmMapper.class})
public interface DirectorMapper {

    DirectorDto toDirectorDto(Director director);

    Director toEntity(DirectorDto directorDto);


}