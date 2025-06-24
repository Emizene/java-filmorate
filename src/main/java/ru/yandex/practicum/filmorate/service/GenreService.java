package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public ResponseEntity<List<GenreDto>> getAllGenres() {
        log.debug("Запрос всех жанров");
        List<GenreDto> genres = genreRepository.findAll().stream()
                .map(genreMapper::toGenreDto)
                .toList();

        log.info("Возращено {} жанров", genres.size());
        return ResponseEntity.ok(genres);
    }

    public ResponseEntity<GenreDto> getGenreById(Long id) {
        log.debug("Запрос жанра с ID: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID %s не найден".formatted(id)));

        log.info("Найден жанр: ID={}, Название={}", id, genre.getName());

        return ResponseEntity.ok(genreMapper.toGenreDto(genre));
    }
}
