package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, UserDto userDto); // Создание запроса

    List<ItemRequestDto> getUserRequests(Integer userId); // Получение запросов конкретного пользователя

    List<ItemRequestDto> getAllRequests(); // Получение всех запросов

    ItemRequestDto getRequestById(Integer requestId); // Получение запроса по ID
}
