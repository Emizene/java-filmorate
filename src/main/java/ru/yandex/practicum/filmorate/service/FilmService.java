package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.ChangeFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;
    private final FilmMapper filmMapper;
    private final GenreMapper genreMapper;
    private final JdbcTemplate jdbcTemplate;
    private final EventService eventService;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final DirectorService directorService;

    public ResponseEntity<List<FilmResponseDto>> getAllFilms() {
        log.debug("Запрос всех фильмов");
        List<FilmResponseDto> films = filmRepository.findAll().stream()
                .map(filmMapper::toFilmDto)
                .toList();
        log.info("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }

    @Transactional
    public ResponseEntity<FilmResponseDto> addFilm(ChangeFilmDto film) {
        log.debug("Попытка добавить фильм: {}", film.getName());

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getMpa() != null && (film.getMpa().getId() < 1 || film.getMpa().getId() > 5)) {
            throw new NotFoundException("Mpa рейтинга с ID %s не существует"
                    .formatted(film.getMpa().getId()));
        }
        if (film.getGenres() != null && film.getGenres().stream()
                .anyMatch(genre -> genre.getId() < 1 || genre.getId() > 6)) {
            throw new NotFoundException("Жанра с таким ID не существует");
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(directorDto -> {
                Optional<Director> existingDirector = directorRepository.findById(directorDto.getId());
                if (existingDirector.isEmpty()) {
                    directorService.addDirector(directorDto);
                }
            });
        }

        Film entity = filmMapper.toEntity(film);
        filmRepository.save(entity);
        log.info("Фильм успешно добавлен: ID={}, Название={}", entity.getId(), entity.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(filmMapper.toFilmDto(entity));
    }

    @Transactional
    public ResponseEntity<FilmResponseDto> updateFilm(ChangeFilmDto film) {
        log.debug("Попытка обновить фильм ID={}", film.getId());

        List<Film> allFilms = filmRepository.findAll();
        Film updateFilm = allFilms.stream()
                .filter(f -> Objects.equals(f.getId(), film.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", film.getId());
                    return new NotFoundException("Фильм с id " + film.getId() + " не найден");
                });

        if (film.getName() != null && !updateFilm.getName().equals(film.getName())) {
            log.debug("Обновление названия фильма с '{}' на '{}'", updateFilm.getName(), film.getName());
            updateFilm.setName(film.getName());
        }
        if (film.getDescription() != null && !film.getDescription().equals(updateFilm.getDescription())) {
            log.debug("Обновление описания фильма");
            updateFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null && !film.getReleaseDate().equals(updateFilm.getReleaseDate())) {
            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            log.debug("Обновление даты релиза с {} на {}", updateFilm.getReleaseDate(), film.getReleaseDate());
            updateFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null && !film.getDuration().equals(updateFilm.getDuration())) {
            if (film.getDuration() <= 0) {
                log.warn("Некорректная продолжительность: {}", film.getDuration());
                throw new ValidationException("Продолжительность должна быть положительной");
            }
            log.debug("Обновление продолжительности с {} на {}", updateFilm.getDuration(), film.getDuration());
            updateFilm.setDuration(film.getDuration());
        }
        if (film.getMpa() != null) {
            if (film.getMpa().getId() < 1 || film.getMpa().getId() > 5) {
                throw new NotFoundException("MPA рейтинга с ID %d не существует".formatted(film.getMpa().getId()));
            }
            Mpa mpaEntity = mpaRepository.findById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден"));
            if (updateFilm.getMpaRating() == null || !updateFilm.getMpaRating().getId().equals(mpaEntity.getId())) {
                log.debug("Обновление MPA рейтинга с {} на {}",
                        updateFilm.getMpaRating() != null ? updateFilm.getMpaRating().getId() : "null",
                        mpaEntity.getId());
                updateFilm.setMpaRating(mpaEntity);
            }
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genreDto -> {
                if (genreDto.getId() < 1 || genreDto.getId() > 6) {
                    throw new NotFoundException("Жанра с ID %d не существует".formatted(genreDto.getId()));
                }
            });
            List<Genre> uniqueGenres = film.getGenres().stream()
                    .distinct()
                    .map(genreMapper::toEntity)
                    .collect(Collectors.toList());
            if (!uniqueGenres.equals(updateFilm.getGenres())) {
                log.debug("Обновление жанров фильма");
                updateFilm.setGenres(uniqueGenres);
            }
        }

        if (film.getDirectors() != null) {
            List<Director> unique = film.getDirectors().stream()
                    .distinct()
                    .map(dir -> {
                        Optional<Director> existingDirector = directorRepository.findById(dir.getId());
                        if (existingDirector.isPresent()) {
                            if (dir.getName() == null || dir.getName().isBlank()) {
                                return existingDirector.get();
                            }
                            Director director = existingDirector.get();
                            director.setName(dir.getName());
                            return directorRepository.save(director);
                        } else {
                            if (dir.getName() == null || dir.getName().isBlank()) {
                                throw new ValidationException("Имя режиссера обязательно!");
                            }
                            return directorRepository.save(directorMapper.toEntity(dir));
                        }
                    })
                    .collect(Collectors.toList());

            if (!unique.equals(updateFilm.getDirectors())) {
                updateFilm.setDirectors(unique);
            }
        }

        filmRepository.save(updateFilm);
        log.info("Фильм успешно обновлен: ID={}", film.getId());
        return ResponseEntity.ok().body(filmMapper.toFilmDto(updateFilm));
    }

    @Transactional
    public ResponseEntity<Void> addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не может быть null");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: ID={}", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        List<User> usersWithLikes = film.getUsersWithLikes();
        if (usersWithLikes.contains(user)) {
            log.warn("Повторный лайк: userID={} уже лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        usersWithLikes.add(user);
        filmRepository.save(film);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);

        log.info("Лайк добавлен: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmID={}, userID={}", filmId, userId);

        if (filmId == null || userId == null) {
            log.warn("Передан null ID: filmID={}, userID={}", filmId, userId);
            throw new ValidationException("ID не был введен");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> {
                    log.error("Фильм не найден: ID={}", filmId);
                    return new NotFoundException("Фильм с id " + filmId + " не найден");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: ID={}", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        List<User> usersWithLikes = film.getUsersWithLikes();
        if (!usersWithLikes.remove(user)) {
            log.warn("Лайк не найден: userID={} не лайкал filmID={}", userId, filmId);
            throw new ValidationException("Пользователь не ставил лайк этому фильму");
        }

        filmRepository.save(film);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);

        log.info("Лайк удален: filmID={}, userID={}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<FilmResponseDto>> getPopularFilms(int count,Long genreId, Integer year) {
        log.debug("Запрос популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);

        if (count <= 0) {
            log.warn("Некорректный параметр count: {}", count);
            throw new ValidationException("Параметр count должен быть положительным числом.");
        }

        if (genreId != null && (genreId < 1 || genreId > 6)) {
            log.warn("Некорректный параметр genreId: {}", genreId);
            throw new ValidationException("Всего 6 жанров. Выбрать число от 1 до 6.");
        }

        if (year != null && year < 1985) {
            log.warn("Некорректный параметр year: {}", year);
            throw new ValidationException("Год создания не раньше 1985.");
        }

        List<Film> popularFilms = filmRepository.findAll().stream()

                .filter(film -> {
                    if (genreId != null && film.getGenres().stream().noneMatch(g -> g.getId().equals(genreId))) {
                        return false;
                    }
                    return year == null || film.getReleaseDate().getYear() == year;
                })
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersWithLikes().size()).reversed())
                .limit(count)
                .toList();
        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return ResponseEntity.ok(filmMapper.toFilmDtoList(popularFilms));
    }

    public ResponseEntity<FilmResponseDto> getFilmById(Long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID %s не найден".formatted(filmId)));

        log.info("Найден фильм: ID={}, Название={}", filmId, film.getName());

        return ResponseEntity.ok(filmMapper.toFilmDto(film));
    }

    @Transactional
    public void deleteAllFilms() {
        log.warn("Удаление всех фильмов");
        filmRepository.deleteAll();
        log.info("Все фильмы удалены");
    }

    @Transactional
    public ResponseEntity<List<FilmResponseDto>> findFilmsByDirectorSorted(Long directorId, String sortBy) {

        log.debug("Попытка отсортировать фильмы режиссера c ID {} по {}", directorId, sortBy);
        Optional<Director> optionalDirector = directorRepository.findById(directorId);

        if (optionalDirector.isEmpty()) {
            log.error("Режиссер с ID {} не найден.", directorId);
            throw new NotFoundException("Режиссёр с указанным ID не найден.");
        }

        Director director1 = optionalDirector.get();

        Specification<Film> spec = (root, query, cb) -> {
            Join<Object, Object> joinDirector = root.join("directors");
            Predicate predicate = cb.equal(joinDirector, director1);

            if ("year".equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.asc(cb.function("YEAR", Integer.class, root.get("releaseDate"))));
            } else if ("likes".equalsIgnoreCase(sortBy)) {
                Expression<Integer> countExpression = cb.size(root.get("usersWithLikes"));
                query.orderBy(cb.desc(countExpression));
            } else {
                throw new IllegalArgumentException("Неверный параметр сортировки: " + sortBy);
            }

            return predicate;
        };

        List<Film> films = filmRepository.findAll(spec);

        if (films.isEmpty()) {
            log.warn("Режиссер найден, но фильмов нет.");
            return ResponseEntity.ok(Collections.emptyList()); // Явно возвращаем пустой список
        } else {
            log.info("Найдено {} фильмов для режиссера с ID {}.", films.size(), directorId);
            return ResponseEntity.ok(filmMapper.toFilmDtoList(films)); // Возвращаем успешный ответ с фильмами
        }
    }

    public List<FilmResponseDto> getRecommendations(Long userId) {
        List<Film> recommendations = filmRepository.findRecommendations(userId);
        log.info("Возвращено {} рекомендованных фильмов", recommendations.size());
        return filmMapper.toFilmDtoList(recommendations);
    }

    @Transactional
    public void deleteFilm(long filmId) {
        log.debug("Начато удаление фильма с ID {}", filmId);

        if (!filmRepository.existsById(filmId)) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с ID %s не найден".formatted(filmId));
        }
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", filmId);
        filmRepository.deleteById(filmId);
        log.info("Фильм с ID {} и все его зависимости успешно удалены", filmId);
    }

    public ResponseEntity<List<FilmResponseDto>> getCommonFilms(Long userId, Long friendId) {
        log.debug("Запрос общих фильмов пользователя ID={} с другом ID={}", userId, friendId);

        List<Film> userFilms = userRepository.findById(userId)
                .map(User::getLikedFilms)
                .orElse(new ArrayList<>());

        List<Film> friendFilms = userRepository.findById(friendId)
                .map(User::getLikedFilms)
                .orElse(new ArrayList<>());

        List<Film> commonFilms = userFilms.stream()
                .filter(friendFilms::contains)
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersWithLikes().size()).reversed())
                .toList();

        log.info("Найдено {} общих фильмов между {} и {}", commonFilms.size(), userId, friendId);

        return ResponseEntity.ok(filmMapper.toFilmDtoList(commonFilms));
    }
}
