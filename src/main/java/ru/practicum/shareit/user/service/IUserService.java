package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface IUserService {
    UserDto addUser(UserDto userDto) throws DuplicateDataException;

    List<UserDto> getUsers();

    UserDto updateUser(Integer userId, UserDto userDto);

    UserDto getUser(Integer id);

    void deleteUser(Integer id);
}
