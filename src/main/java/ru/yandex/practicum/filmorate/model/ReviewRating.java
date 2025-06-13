package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Valid
@Entity
@Builder
@Table(name = "review_rating")
@NoArgsConstructor
public class ReviewRating {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToMany
    @JoinTable(
            name = "review_likes",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usersLikes;

    @OneToMany
    @JoinTable(
            name = "review_dislikes",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usersDislikes;

}
