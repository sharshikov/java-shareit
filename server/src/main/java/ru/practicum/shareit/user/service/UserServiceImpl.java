package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateDataException("Пользователь с таким email уже существует");
        }
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NotFoundDataException("Пользователь не найден"));
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundDataException("Пользователь не найден"));
        if (userDto.getName() != null)
            existingUser.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            var userWithSameEmail = userRepository.findByEmail(userDto.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new DuplicateDataException("Пользователь с таким email уже существует");
            }
            existingUser.setEmail(userDto.getEmail());
        }
        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
