package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorDto {
    private Long id;

    @NotBlank(message = "Имя режиссёра обязательно")
    private String name;
}
