package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("Запрос всех пользователей");
        List<User> users = userStorage.getAllUsers();
        log.info("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> createUser(User user) {
        log.debug("Создание нового пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        List<User> allUsers = userStorage.getAllUsers();
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
        userStorage.createUser(user);
        log.info("Успешное создание пользователя: ID={}, login={}", user.getId(), user.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public ResponseEntity<User> updateUser(User user) {
        log.debug("Обновление существующего пользователя с ID {}", user.getId());
        List<User> allUsers = userStorage.getAllUsers();
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

        userStorage.updateUser(updatedUser);
        log.info("Пользователь с ID {} успешно обновлен", user.getId());
        return ResponseEntity.ok(updatedUser);
    }

    public ResponseEntity<Void> addFriend(Long userId, Long friendId) {
        log.debug("Попытка добавления друга {} пользователю {}", friendId, userId);

        if (userId == null || friendId == null) {
            log.warn("Ошибка добавления друга: не указан ID пользователя или друга");
            throw new NotFoundException("ID не был введен");
        }

        if (userId.equals(friendId)) {
            log.warn("Пользователь {} попытался добавить самого себя в друзья", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь {} уже есть в друзьях у {}", friendId, userId);
            throw new ValidationException("Пользователь уже в друзьях");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователь {} успешно добавлен в друзья к {}", friendId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteFriend(Long userId, Long friendId) {
        log.debug("Попытка удаления друга {} у пользователя {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь {} удален из друзей у {}", friendId, userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<User>> getCommonFriends(Long user1Id, Long user2Id) {
        log.debug("Поиск общих друзей пользователей {} и {}", user1Id, user2Id);

        Set<Long> user1Friends = userStorage.getUserById(user1Id).getFriends();
        Set<Long> user2Friends = userStorage.getUserById(user2Id).getFriends();

        List<Long> commonFriends = user1Friends.stream()
                .filter(user2Friends::contains)
                .toList();

        List<User> commonFriendUsers = commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());

        log.info("Найдено {} общих друзей между {} и {}", commonFriendUsers.size(), user1Id, user2Id);
        return ResponseEntity.ok(commonFriendUsers);
    }

    public ResponseEntity<List<User>> getFriends(Long userId) {
        log.debug("Запрос друзей пользователя {}", userId);

        if (userId == null || userId < 0) {
            throw new NotFoundException("Некорректный ID пользователя");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID %s не найден".formatted(userId));
        }

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
            User friend = userStorage.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            } else {
                throw new NotFoundException("Друг с ID %s не найден".formatted(friendId));
            }
        }

        log.info("Найдено {} друзей для пользователя {}", friends.size(), userId);
        return ResponseEntity.ok(friends);
    }

    public ResponseEntity<User> getUserById(Long userId) {
        if (userId == null || userId < 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID %s не найден".formatted(userId));
        }
        log.info("Найден пользователь: ID={}, Логин={}", userId, user.getLogin());
        return ResponseEntity.ok(user);
    }

    public void deleteAllUsers() {
        log.warn("Выполняется запрос на удаление всех пользователей");
        userStorage.deleteAll();
        log.info("Все пользователи удалены");
    }
}
