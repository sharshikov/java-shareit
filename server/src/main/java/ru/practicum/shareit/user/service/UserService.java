package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(Integer id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Integer id, UserDto userDto);

    void deleteUser(Integer id);
}
