package ru.practicum.shareit.request.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final UserService userService; // Сервис для получения информации о пользователе

    public ItemRequestController(ItemRequestService itemRequestService, UserService userService) {
        this.itemRequestService = itemRequestService;
        this.userService = userService;
    }

    // Создание нового запроса на предмет
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody ItemRequestDto itemRequestDto) {

        UserDto currentUser = userService.getUserById(userId); // Получаем текущего пользователя по userId
        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestDto, currentUser);

        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    // Получение списка своих запросов
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    // Получение списка всех запросов от других пользователей
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests() {
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests();
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    // Получение данных об одном запросе по ID
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @PathVariable Integer requestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemRequestDto requestDto = itemRequestService.getRequestById(requestId);
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }
}
