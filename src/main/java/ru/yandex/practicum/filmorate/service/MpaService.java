package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaRepository mpaRepository;
    private final MpaMapper mpaMapper;

    public ResponseEntity<List<MpaDto>> getAllMpa() {
        log.debug("Запрос всех рейтингов");

        List<MpaDto> mpa = mpaRepository.findAll().stream()
                .map(mpaMapper::toMpaDto)
                .toList();

        log.info("Возвращено {} рейтингов", mpa.size());
        return ResponseEntity.ok(mpa);
    }

    public ResponseEntity<MpaDto> getMpaById(Long id) {
        log.debug("Запрос рейтинга с ID: {}", id);

        Mpa mpa = mpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID %s не найден".formatted(id)));

        log.info("Найден рейтинг: ID={}, Название={}", id, mpa.getName());

        return ResponseEntity.ok(mpaMapper.toMpaDto(mpa));
    }

}
