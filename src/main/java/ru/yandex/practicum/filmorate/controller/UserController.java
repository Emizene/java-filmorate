package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Valid
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody ChangeUserDto user) {
        return userService.createUser(user);
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody ChangeUserDto user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserResponseDto>> getFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{user1Id}/friends/common/{user2Id}")
    public ResponseEntity<List<UserResponseDto>> getCommonFriends(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        return userService.getCommonFriends(user1Id, user2Id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> gerUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/recommendations")
    public ResponseEntity<List<FilmResponseDto>> getRecommendations(@PathVariable Long userId) {
        return userService.getRecommendations(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/feed")
    public ResponseEntity<List<EventDto>> getEventFeed(@PathVariable Long userId) {
        return userService.getUserEvents(userId);
    }
}
