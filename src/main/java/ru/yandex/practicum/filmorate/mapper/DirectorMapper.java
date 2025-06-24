package ru.yandex.practicum.filmorate.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Mapper(componentModel = "spring", uses = {FilmMapper.class})
public interface DirectorMapper {
    @Mapping(target = "id", ignore = true)
    Director toEntity(DirectorDto directorDto);

    DirectorDto toDirectorDto(Director director);
}
