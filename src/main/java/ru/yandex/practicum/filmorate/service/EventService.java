package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository repository;
    private final EventMapper mapper;

    @Transactional
    public void createEvent(Long userId, EventType eventType, EventOperation operation, Long entityId) {
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .timestamp(Instant.now())
                .build();
        repository.save(event);
        log.debug("Добавлено событие {} для пользователя {}", event.getEventId(), userId);
    }

    public List<EventDto> getEvents(Long userId) {
        List<EventDto> events = repository.findByUserIdOrderByEventIdAsc(userId)
                .stream()
                .map(mapper::toEventDto)
                .toList();
        log.info("Возвращено {} событий", events.size());
        return events;
    }
}