package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    // Создание нового запроса на предмет
    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    // Получение списка своих запросов
    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemRequestClient.getUserRequests(userId);
    }

    // Получение списка всех запросов от других пользователей
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        return itemRequestClient.getAllRequests();
    }

    // Получение данных об одном запросе по ID
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable Integer requestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
