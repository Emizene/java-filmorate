package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    public ResponseEntity<List<DirectorDto>> getAllDirectors() {
        log.debug("Запрос всех режессеров");
        List<DirectorDto> directors = directorRepository.findAll().stream()
                .map(directorMapper::toDirectorDto)
                .toList();
        log.info("Возвращено {} режиссеров", directors.size());
        return ResponseEntity.ok(directors);
    }

    public ResponseEntity<DirectorDto> getDirectorById(Long id) {
        log.debug("Запрос режиссера с ID: {}", id);
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID %s не найден".formatted(id)));
        String name = director.getName();
        log.info("Найден режиссер: ID={}, Имя={}", id, name);
        return ResponseEntity.ok(directorMapper.toDirectorDto(director));
    }


    @Transactional
    public ResponseEntity<DirectorDto> addDirector(DirectorDto director) {
        log.debug("Попытка добавить режиссера: {}", director.getName());
        if (directorRepository.existsByName(director.getName())) {
            log.warn("Режиссер с таким именем уже существует: {}", director.getName());
            throw new ValidationException("Режиссер с именем '" + director.getName() + "' уже существует");
        }
        Director entity = directorMapper.toEntity(director);
        directorRepository.save(entity);
        log.info("Режиссер успешно добавлен: ID={}, Имя={}", entity.getId(), entity.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(directorMapper.toDirectorDto(entity));
    }

    @Transactional
    public ResponseEntity<DirectorDto> updateDirector(DirectorDto director) {
        log.debug("Попытка обновить режиссера ID={}", director.getId());
        Director existingDirector = directorRepository.findById(director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + director.getId() + " не найден"));

        if (director.getName() != null && !director.getName().isEmpty() && !existingDirector.getName().equals(director.getName())) {
            if (directorRepository.existsByName(director.getName())) {
                log.warn("Режиссер с таким именем уже существует: {}", director.getName());
                throw new ValidationException("Режиссер с именем '" + director.getName() + "' уже существует");
            }
            log.debug("Обновление имени режиссера с '{}' на '{}'", existingDirector.getName(), director.getName());
            existingDirector.setName(director.getName());
        }

        directorRepository.save(existingDirector);
        log.info("Режиссер успешно обновлен: ID={}", director.getId());
        return ResponseEntity.ok().body(directorMapper.toDirectorDto(existingDirector));
    }

    public ResponseEntity<Void> deleteDirector(Long id) {
        log.debug("Попытка удалить режиссера ID={}", id);
        directorRepository.deleteById(id);
        log.info("Режиссер успешно удален: ID={}", id);
        return ResponseEntity.noContent().build();
    }
}