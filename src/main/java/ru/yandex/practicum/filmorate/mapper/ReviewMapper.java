package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Review;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "filmId", source = "film.id")
//    @Mapping(target = "useful", defaultValue = "0")
//    @Mapping(target = "useful", expression = "java(review.getUsersWithLikesOnReviews().size()-review.getUsersWithDislikesOnReviews().size())")
    public abstract ReviewResponseDto toReviewDto(Review review);

    @Mapping(target = "user", expression = "java(userRepository.findById(changeReviewDto.getUserId()).orElseThrow())")
    @Mapping(target = "film", expression = "java(filmRepository.findById(changeReviewDto.getFilmId()).orElseThrow())")
    public abstract Review toEntity(ChangeReviewDto changeReviewDto);
}