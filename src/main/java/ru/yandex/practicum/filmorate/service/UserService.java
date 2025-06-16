package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dto.ChangeUserDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FilmService filmService;
    private final FilmRepository filmRepository;

    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.debug("Запрос всех пользователей");
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
        log.info("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<UserResponseDto> createUser(ChangeUserDto user) {
        log.debug("Создание нового пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        List<User> allUsers = userRepository.findAll().stream()
                .toList();
        boolean emailExists = allUsers.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            log.warn("Этот имейл уже используется: {}", user.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
        boolean loginExists = allUsers.stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(user.getLogin()));
        if (loginExists) {
            log.warn("Этот логин уже используется: {}", user.getLogin());
            throw new ValidationException("Этот логин уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User entity = userMapper.toEntity(user);
        userRepository.save(entity);
        log.info("Успешное создание пользователя: ID={}, login={}", user.getId(), user.getLogin());

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserDto(entity));
    }

    @Transactional
    public ResponseEntity<UserResponseDto> updateUser(ChangeUserDto user) {
        log.debug("Обновление существующего пользователя с ID {}", user.getId());
        List<User> allUsers = userRepository.findAll().stream()
                .toList();
        User updatedUser = allUsers.stream()
                .filter(u -> Objects.equals(u.getId(), user.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для обновления", user.getId());
                    return new NotFoundException("Пользователь с id " + user.getId() + " не найден");
                });

        if (user.getLogin() != null && !updatedUser.getLogin().equals(user.getLogin())) {
            log.debug("Обновление логина пользователя с {} на {}", updatedUser.getLogin(), user.getLogin());
            updatedUser.setLogin(user.getLogin());
        }

        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            boolean emailExists = allUsers.stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()));
            if (emailExists) {
                log.warn("Пользователь с email {} уже существует", user.getEmail());
                throw new ValidationException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            log.debug("Обновление email пользователя с {} на {}", updatedUser.getEmail(), user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }

        if (user.getBirthday() != null && !updatedUser.getBirthday().equals(user.getBirthday())) {
            log.debug("Обновление даты рождения пользователя");
            updatedUser.setBirthday(user.getBirthday());
        }

        if (user.getName() != null && !updatedUser.getName().equals(user.getName())) {
            log.debug("Обновление имени пользователя с {} на {}", updatedUser.getName(), user.getName());
            updatedUser.setName(user.getName());
        }

        userRepository.save(updatedUser);
        log.info("Пользователь с ID {} успешно обновлен", user.getId());

        return ResponseEntity.ok().body(userMapper.toUserDto(updatedUser));
    }

    @Transactional
    public ResponseEntity<Void> addFriend(Long userId, Long friendId) {
        log.debug("Попытка добавления друга {} пользователю {}", friendId, userId);

        if (userId == null || friendId == null) {
            log.warn("Ошибка добавления друга: не указан ID пользователя или друга");
            throw new NotFoundException("ID не был введен");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден" + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с таким ID не найден" + friendId));

        if (userId.equals(friendId)) {
            log.warn("Пользователь {} попытался добавить самого себя в друзья", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        if (user.getFriends().contains(friend)) {
            log.warn("Пользователь {} уже есть в друзьях у {}", friendId, userId);
            throw new ValidationException("Пользователь уже в друзьях");
        }

        user.getFriends().add(friend);

        userRepository.save(user);

        log.info("Пользователь {} успешно добавлен в друзья к {}", friendId, userId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteFriend(Long userId, Long friendId) {
        log.debug("Попытка удаления друга {} у пользователя {}", friendId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(userId)));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(friendId)));

        user.getFriends().remove(friend);

        log.info("Пользователь {} удален из друзей у {}", friendId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<UserResponseDto>> getCommonFriends(Long user1Id, Long user2Id) {
        log.debug("Поиск общих друзей пользователей {} и {}", user1Id, user2Id);

        Set<User> user1Friends = userRepository.findById(user1Id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + user1Id + " не найден"))
                .getFriends();
        Set<User> user2Friends = userRepository.findById(user2Id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + user2Id + " не найден"))
                .getFriends();

        List<User> commonFriends = user1Friends.stream()
                .filter(user2Friends::contains)
                .toList();

        log.info("Найдено {} общих друзей между {} и {}", commonFriends.size(), user1Id, user2Id);
        return ResponseEntity.ok(userMapper.toUserDtoList(commonFriends));
    }

    public ResponseEntity<List<UserResponseDto>> getFriends(Long userId) {
        log.debug("Запрос друзей пользователя {}", userId);

        if (userId == null || userId < 0) {
            throw new NotFoundException("Некорректный ID пользователя");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));

        List<User> friends = new ArrayList<>(
                Optional.ofNullable(user.getFriends())
                        .orElse(Collections.emptySet())
        );

        log.info("Найдено {} друзей для пользователя {}", friends.size(), userId);
        return ResponseEntity.ok(userMapper.toUserDtoList(friends));
    }

    public ResponseEntity<UserResponseDto> getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(userId)));

        log.info("Найден пользователь: ID={}, Логин={}", userId, user.getLogin());

        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @Transactional
    public void deleteAllUsers() {
        log.warn("Выполняется запрос на удаление всех пользователей");
        userRepository.deleteAll();
        log.info("Все пользователи удалены");
    }

    public ResponseEntity<List<FilmResponseDto>> getRecommendations(Long userId) {
        log.debug("Выполняется запрос на получение рекомендаций для пользователя {}", userId);
        return ResponseEntity.ok(filmService.getRecommendations(userId));
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(userId)));

        // Удалить связи, где пользователь добавлен в друзья другими
        Set<User> usersWhoAdded = userRepository.findUsersWhoAddedAsFriend(userId);
        usersWhoAdded.forEach(u -> u.getFriends().remove(user));
        userRepository.saveAll(usersWhoAdded);

        // Удалить дружеские связи пользователя
        user.getFriends().clear();

        // Удалить лайки
        List<Film> likedFilmsCopy = new ArrayList<>(user.getLikedFilms());
        likedFilmsCopy.forEach(film -> {
            film.getUsersWithLikes().remove(user);
        });
        filmRepository.saveAll(likedFilmsCopy);

        // Удалить самого пользователя
        userRepository.delete(user);
    }
}