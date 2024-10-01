package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserMapper userMapper;

    private ItemRequestDto testItemRequestDto;
    private User testUser;

    @BeforeEach
    void setup() {
        // Создание тестового пользователя
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        // Создание DTO для тестового запроса
        testItemRequestDto = new ItemRequestDto();
        testItemRequestDto.setDescription("Test description");
        testItemRequestDto.setUserId(testUser.getId());
        testItemRequestDto.setCreated(LocalDateTime.now().toString());  // Преобразование времени в строку
    }

    @Test
    void whenCreateItemRequest_thenRequestIsCreated() {
        // Преобразуем User в UserDto
        UserDto testUserDto = userMapper.toDto(testUser);

        // Создание запроса через сервис
        ItemRequestDto savedRequest = itemRequestService.createRequest(testItemRequestDto, testUserDto);

        // Проверка, что запрос был успешно создан
        assertNotNull(savedRequest.getId());
        assertEquals(testItemRequestDto.getDescription(), savedRequest.getDescription());
        assertEquals(testUserDto.getId(), savedRequest.getUserId());
    }

    @Test
    void whenGetItemRequestById_withNonExistingRequest_thenThrowException() {
        // Ожидаем исключение при попытке получить несуществующий запрос
        assertThrows(RuntimeException.class, () -> itemRequestService.getRequestById(999));
    }

    @Test
    void whenGetAllItemRequests_thenListOfRequestsIsReturned() {
        // Создание и сохранение нескольких запросов
        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setUser(testUser);
        request1.setCreated(LocalDateTime.now());

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setUser(testUser);
        request2.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        // Получение всех запросов через сервис
        List<ItemRequestDto> requests = itemRequestService.getAllRequests();

        // Проверка, что список содержит созданные запросы
        assertEquals(2, requests.size());
        assertEquals(request1.getDescription(), requests.get(0).getDescription());
        assertEquals(request2.getDescription(), requests.get(1).getDescription());
    }

    @Test
    void whenGetUserRequests_thenListOfUserRequestsIsReturned() {
        // Создание и сохранение нескольких запросов от пользователя
        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setUser(testUser);
        request1.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request1);

        // Получение всех запросов пользователя через сервис
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(testUser.getId());

        // Проверка, что список содержит созданные запросы
        assertEquals(1, requests.size());
        assertEquals(request1.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void whenGetUserRequests_withNonExistingUser_thenThrowException() {
        // Проверка, что возникает исключение при запросе для несуществующего пользователя
        assertThrows(RuntimeException.class, () -> itemRequestService.getUserRequests(999));
    }
}
