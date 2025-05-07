package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public void createUser(User user) {
        user.setId(getNextId());
        users.add(user);
    }

    @Override
    public void updateUser(User user) {
        Optional<User> userOptional = users.stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findFirst();
        if (userOptional.isPresent()) {
            users.remove(userOptional.get());
            users.add(user);
        }
    }

    @Override
    public User getUserById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public void deleteUser(Long id) {
        users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .ifPresent(users::remove);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    private long getNextId() {
        long currentMaxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
