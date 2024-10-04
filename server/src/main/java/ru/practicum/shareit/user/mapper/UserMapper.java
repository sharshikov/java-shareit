package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring") // Используем Spring для управления бинами
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
