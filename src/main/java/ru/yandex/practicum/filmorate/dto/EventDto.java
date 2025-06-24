package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.time.Instant;

@Data
@Builder
public class EventDto {
    private Long eventId;
    private Long userId;
    private EventType eventType;
    private EventOperation operation;
    private Long entityId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;
}
