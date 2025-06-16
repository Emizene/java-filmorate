package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Mapper(componentModel = "spring", uses = {FilmMapper.class})
public interface DirectorMapper {

    DirectorDto toDirectorDto(Director director);

    Director toEntity(DirectorDto directorDto);
}