package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Valid
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Это поле обязательно для заполнения")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @Column(length = 200)
    private String review;

    @NotNull
    private Boolean isPositive;

//    @ManyToMany
//    @JoinTable(name = "review_likes",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> usersWithLikesOnReviews = new HashSet<>();
//
//    @ManyToMany
//    @JoinTable(name = "review_dislikes",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> usersWithDislikesOnReviews = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
