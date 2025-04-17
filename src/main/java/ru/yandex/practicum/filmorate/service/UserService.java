package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {

    private final List<User> allUsers = new ArrayList<>();
    private final Validator validator;

    public UserService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage());
        }
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(allUsers);
    }

    public ResponseEntity<User> createUser(User user) {
        boolean emailExists = allUsers.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            throw new ValidationException("Этот имейл уже используется");
        }
        boolean loginExists = allUsers.stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(user.getLogin()));
        if (loginExists) {
            throw new ValidationException("Этот логин уже используется");
        }

        user.setId(getNextId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        allUsers.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public ResponseEntity<User> updateUser(User user) {
        User oldUser = allUsers.stream()
                .filter(u -> Objects.equals(u.getId(), user.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь с id " + user.getId() + " не найден"));

        if (user.getLogin() != null && !oldUser.getLogin().equals(user.getLogin())) {
            oldUser.setLogin(user.getLogin());
        }

        if (user.getEmail() != null && !oldUser.getEmail().equals(user.getEmail())) {
            boolean emailExists = allUsers.stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()));
            if (emailExists) {
                throw new ValidationException("Пользователь с email '" + user.getEmail() + "' уже существует");
            }
            oldUser.setEmail(user.getEmail());
        }

        if (user.getBirthday() != null && !oldUser.getBirthday().equals(user.getBirthday())) {
            oldUser.setBirthday(user.getBirthday());
        }

        if (user.getName() != null && !oldUser.getName().equals(user.getName())) {
            oldUser.setName(user.getName());
        }

        return ResponseEntity.ok(oldUser);
    }

    public void deleteAllUsers() {
        allUsers.clear();
    }

    private long getNextId() {
        long currentMaxId = allUsers.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
