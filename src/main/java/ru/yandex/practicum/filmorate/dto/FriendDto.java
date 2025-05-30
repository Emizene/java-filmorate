package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class FriendDto {
    private Long id;
    private String name;
    private String status; // confirmed/unconfirmed
}
