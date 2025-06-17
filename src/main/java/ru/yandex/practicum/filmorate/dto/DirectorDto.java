package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorDto {
    @NotNull(groups = OnUpdate.class, message = "ID обязателен при обновлении")
    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Имя режиссёра обязательно при создании")
    @JsonProperty("name")
    private String name;
}
