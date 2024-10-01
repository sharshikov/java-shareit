package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUserDto = new UserDto();
        testUserDto.setName("Test User");
        testUserDto.setEmail("testuser@example.com");
    }

    @Test
    void whenCreateUser_thenUserIsCreated() {
        // Создаем пользователя
        UserDto createdUser = userService.createUser(testUserDto);

        // Проверяем, что пользователь был создан
        assertNotNull(createdUser.getId());
        assertEquals(testUserDto.getName(), createdUser.getName());
        assertEquals(testUserDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void whenCreateUser_withExistingEmail_thenThrowDuplicateDataException() {
        // Создаем первого пользователя
        userService.createUser(testUserDto);

        // Попытка создать второго пользователя с тем же email
        assertThrows(DuplicateDataException.class, () -> {
            userService.createUser(testUserDto);
        });
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        // Создаем пользователя и сохраняем его
        UserDto createdUser = userService.createUser(testUserDto);

        // Получаем пользователя по ID
        UserDto foundUser = userService.getUserById(createdUser.getId());

        // Проверяем, что пользователь был найден и данные совпадают
        assertNotNull(foundUser);
        assertEquals(createdUser.getName(), foundUser.getName());
        assertEquals(createdUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void whenGetUserById_withNonExistingUser_thenThrowNotFoundDataException() {
        // Ожидаем исключение при попытке получить несуществующего пользователя
        assertThrows(NotFoundDataException.class, () -> userService.getUserById(999));
    }

    @Test
    void whenUpdateUser_thenUserIsUpdated() {
        // Создаем и сохраняем пользователя
        UserDto createdUser = userService.createUser(testUserDto);

        // Обновляем данные пользователя
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Updated User");
        updatedUserDto.setEmail("updated@example.com");
        UserDto updatedUser = userService.updateUser(createdUser.getId(), updatedUserDto);

        // Проверяем, что данные были обновлены
        assertEquals(updatedUserDto.getName(), updatedUser.getName());
        assertEquals(updatedUserDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    void whenUpdateUser_withExistingEmail_thenThrowDuplicateDataException() {
        // Создаем двух пользователей
        UserDto firstUser = new UserDto();
        firstUser.setName("First User");
        firstUser.setEmail("first@example.com");
        userService.createUser(firstUser);

        UserDto secondUser = new UserDto();
        secondUser.setName("Second User");
        secondUser.setEmail("second@example.com");
        UserDto secondCreatedUser = userService.createUser(secondUser);

        // Попытка обновить email второго пользователя на email первого пользователя
        UserDto updateUserDto = new UserDto();
        updateUserDto.setEmail("first@example.com");
        assertThrows(DuplicateDataException.class, () -> {
            userService.updateUser(secondCreatedUser.getId(), updateUserDto);
        });
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        // Создаем пользователя и сохраняем его
        UserDto createdUser = userService.createUser(testUserDto);

        // Удаляем пользователя
        userService.deleteUser(createdUser.getId());

        // Проверяем, что пользователь был удален
        Optional<User> deletedUser = userRepository.findById(createdUser.getId());
        assertTrue(deletedUser.isEmpty());
    }
}
