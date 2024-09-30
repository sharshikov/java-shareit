package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto testUserDto;

    @BeforeEach
    void setup() {
        // Создание DTO для тестового пользователя
        testUserDto = new UserDto();
        testUserDto.setName("Test User");
        testUserDto.setEmail("testuser@example.com");
    }

    @Test
    void whenCreateUser_thenUserIsCreated() {
        // Создание пользователя через сервис
        UserDto savedUser = userService.createUser(testUserDto);

        // Проверка, что пользователь был успешно создан
        assertNotNull(savedUser.getId());
        assertEquals(testUserDto.getName(), savedUser.getName());
        assertEquals(testUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void whenGetUserById_thenUserIsReturned() {
        // Создание пользователя через репозиторий
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        // Получение пользователя через сервис
        UserDto foundUser = userService.getUserById(user.getId());

        // Проверка, что возвращенный пользователь соответствует сохраненному
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void whenUpdateUser_thenUserIsUpdated() {
        // Создание и сохранение пользователя
        User user = new User();
        user.setName("Old Name");
        user.setEmail("oldemail@example.com");
        userRepository.save(user);

        // DTO для обновления
        UserDto updateUserDto = new UserDto();
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updatedemail@example.com");

        // Обновление пользователя через сервис
        UserDto updatedUser = userService.updateUser(user.getId(), updateUserDto);

        // Проверка, что данные были обновлены
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updatedemail@example.com", updatedUser.getEmail());
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        // Создание и сохранение пользователя
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        // Удаление пользователя через сервис
        userService.deleteUser(user.getId());

        // Проверка, что пользователь был удален
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void whenGetAllUsers_thenListOfUsersIsReturned() {
        // Создание и сохранение пользователей
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");

        userRepository.save(user1);
        userRepository.save(user2);

        // Получение всех пользователей через сервис
        var users = userService.getAllUsers();

        // Проверка, что список содержит созданных пользователей
        assertEquals(2, users.size());
    }
}
