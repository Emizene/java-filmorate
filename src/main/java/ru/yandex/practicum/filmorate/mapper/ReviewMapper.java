package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.ChangeReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    // Маппинг для создания нового отзыва
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(mapUser(dto.getUserId()))")
    @Mapping(target = "film", expression = "java(mapFilm(dto.getFilmId()))")
    Review toEntity(ChangeReviewDto dto);

    @Mapping(target = "reviewId", source = "id")
    @Mapping(target = "useful", expression = "java(calculateUseful(review))")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "filmId", source = "film.id")
    ReviewResponseDto toReviewDto(Review review);

    default int calculateUseful(Review review) {
        if (review.getRating() == null) return 0;
        return review.getRating().getUsersLikes().size() -
                review.getRating().getUsersDislikes().size();
    }

    default User mapUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Film mapFilm(Long filmId) {
        Film film = new Film();
        film.setId(filmId);
        return film;
    }
}