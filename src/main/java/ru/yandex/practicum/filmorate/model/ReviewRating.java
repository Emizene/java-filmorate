//package ru.yandex.practicum.filmorate.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Set;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@Valid
//@Entity
//@Table(name = "review_rating")
//@NoArgsConstructor
//public class ReviewRating {
//
//    @Id
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "review_id")
//    private Review review;
//
//    @OneToMany
//    private Set<User> users;
//
//}
