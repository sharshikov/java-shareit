package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static int id = 1;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(UserDto userDto) {
        if (userStorage.getUsers().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            throw new DuplicateDataException("Пользователь с таким email уже существует");
        }
        userDto.setId(id);
        User user = UserMapper.INSTANCE.toUser(userDto);
        userStorage.addUser(user);
        id++;
        return userDto;
    }

    public List<UserDto> getUsers() {
        return UserMapper.INSTANCE.tooUsersDto(userStorage.getUsers());
    }

    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userStorage.getUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundDataException("Пользователь с таким id не найден"));
        if (userDto.getEmail() != null) {
            if (userStorage.getUsers().stream()
                    .anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && !u.getId().equals(userId))) {
                throw new DuplicateDataException("Пользователь с таким email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public UserDto getUser(Integer id) {
        return userStorage.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .map(UserMapper.INSTANCE::toUserDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundDataException("Пользователь с таким id не найден"));
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }
}