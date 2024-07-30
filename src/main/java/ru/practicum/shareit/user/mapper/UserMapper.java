package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "id")
    User toUser(UserDto userDto);

    @Mapping(source = "id", target = "id")
    UserDto toUserDto(User user);

    @Mapping(source = "id", target = "id")
    List<UserDto> toUserDtos(List<User> users);
}
