package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRatingRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewRating;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FilmRepository filmRepository;
    @Autowired
    ReviewRatingRepository reviewRatingRepository;

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "filmId", source = "film.id")
    @Mapping(target = "reviewId", source = "id")
    @Mapping(target = "useful", source = "review.id", qualifiedByName = "mapUseful")
    public abstract ReviewResponseDto toReviewDto(Review review);

    @Mapping(target = "user", expression = "java(userRepository.findById(changeReviewDto.getUserId()).orElseThrow())")
    @Mapping(target = "film", expression = "java(filmRepository.findById(changeReviewDto.getFilmId()).orElseThrow())")
    @Mapping(target = "id", source = "reviewId")
    public abstract Review toEntity(ChangeReviewDto changeReviewDto);

    @Named("mapUseful")
    Integer mapUseful(Long reviewId) {
        ReviewRating reviewRating = reviewRatingRepository.findByReviewId(reviewId).orElseThrow();
        return reviewRating.getUsersLikes().size() - reviewRating.getUsersDislikes().size();
    }
}