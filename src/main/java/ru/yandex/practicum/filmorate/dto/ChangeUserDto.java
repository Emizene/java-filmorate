package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ChangeUserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Long> friendsId;
    private List<Long> filmsId;
}
