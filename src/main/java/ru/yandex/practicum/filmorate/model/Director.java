package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Valid
@NoArgsConstructor
@Entity
@Table(name = "directors")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank(message = "Имя режиссёра не может состоять из пробелов.")
   // @NotNull(message = "Имя режиссёра обязательно.")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return Objects.equals(id, director.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}