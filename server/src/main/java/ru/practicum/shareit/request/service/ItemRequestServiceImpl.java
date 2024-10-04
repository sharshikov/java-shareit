package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository; // Репозиторий для работы с запросами
    private final ItemRepository itemRepository; // Репозиторий для работы с запросами
    private final ItemRequestMapper itemRequestMapper; // Маппер для преобразования запросов
    private final ItemMapper itemMapper;
    private final UserService userService; // Сервис для работы с пользователями
    private final UserMapper userMapper; // Маппер для преобразования пользователей

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserService userService,
                                  ItemMapper itemMapper,
                                  ItemRequestMapper itemRequestMapper,
                                  UserMapper userMapper) {
        this.itemRequestRepository = itemRequestRepository; // Инициализация репозитория
        this.itemRepository = itemRepository; // Инициализация маппера запросов// Инициализация маппера запросов
        this.userService = userService; // Инициализация сервиса пользователей
        this.userMapper = userMapper; // Инициализация маппера пользователей
        this.itemMapper = itemMapper; // Инициализация маппера пользователей
        this.itemRequestMapper = itemRequestMapper; // Инициализация маппера пользователей
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, UserDto userDto) {
        // Преобразование DTO в сущность
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        // Получаем пользователя как сущность
        User user = userMapper.toEntity(userDto); // Преобразуем UserDto в User
        itemRequest.setUser(user); // Устанавливаем пользователя
        // Сохранение в базе данных
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        // Возвращаем DTO
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Integer userId) {
        // Получаем пользователя по его ID
        UserDto userDto = userService.getUserById(userId); // Получаем UserDto
        User user = userMapper.toEntity(userDto); // Преобразуем UserDto в User

        // Получаем запросы пользователя
        List<ItemRequest> requests = itemRequestRepository.findByUser(user); // Находим запросы по пользователю
        return requests.stream()
                .map(itemRequestMapper::toDto) // Преобразуем в ItemRequestDto
                .collect(Collectors.toList()); // Собираем в список
    }


    @Override
    public List<ItemRequestDto> getAllRequests() {
        // Получаем все запросы
        List<ItemRequest> allRequests = itemRequestRepository.findAll();
        return allRequests.stream()
                .map(itemRequestMapper::toDto) // Преобразуем в ItemRequestDto
                .collect(Collectors.toList()); // Собираем в список
    }

    @Override
    public ItemRequestDto getRequestById(Integer requestId) {
        // Ищем запрос по ID
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден")); // Обработка ошибки
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemsForRequest = items.stream().map(itemMapper::toDto).toList();
        // Возвращаем DTO
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(itemsForRequest);
        return itemRequestDto;
    }
}
