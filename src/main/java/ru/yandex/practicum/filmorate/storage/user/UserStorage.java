package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

     List<User> getAllUsers();

     void deleteAll();

     void updateUser(User user);

     void createUser(User user);

     User getUserById(Long id);

     void deleteUser(Long id);
}
