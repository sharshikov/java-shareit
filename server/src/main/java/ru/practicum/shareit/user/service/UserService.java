package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(Integer id);

    UserDto updateUser(Integer id, UserDto userDto);

    void deleteUser(Integer id);
}
