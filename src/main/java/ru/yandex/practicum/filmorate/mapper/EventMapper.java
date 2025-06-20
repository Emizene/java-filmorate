package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "eventId", source = "eventId")
    EventDto toEventDto(Event entity);
}
