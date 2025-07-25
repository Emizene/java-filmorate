package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(ChangeUserDto changeUserDto);

    UserResponseDto toUserDto(User user);

    List<UserResponseDto> toUserDtoList(List<User> user);

    FriendDto toFriendDto(User user);
}
