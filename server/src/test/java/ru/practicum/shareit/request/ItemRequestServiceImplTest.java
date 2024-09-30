package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void whenGetItemRequestById_thenRequestIsReturned() {
        // Создание запроса через репозиторий
        ItemRequest request = new ItemRequest();
        request.setDescription("Test description");
        request.setUser(testUser);
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);

        // Получение запроса через сервис
        ItemRequestDto foundRequest = itemRequestService.getRequestById(request.getId());

        // Проверка, что возвращенный запрос соответствует сохраненному
        assertNotNull(foundRequest);
        assertEquals(request.getId(), foundRequest.getId());
        assertEquals(request.getDescription(), foundRequest.getDescription());
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
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(testUser.getId());

        // Проверка, что список содержит созданные запросы
        assertEquals(2, requests.size());
    }
}
