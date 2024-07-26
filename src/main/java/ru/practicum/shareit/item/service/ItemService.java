package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserService userService;
    private static int id = 1;

    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        userService.getUser(userId);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setId(id++);
        item.setOwner(userId);
        itemStorage.addItem(item);
        return ItemMapper.INSTANCE.toItemDto(item);
    }

    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemStorage.getItems().stream()
                .filter(i -> i.getId().equals(itemId) && i.getOwner().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundDataException("Item с таким id не найден"));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.INSTANCE.toItemDto(item);
    }

    public ItemDto getItemById(Integer itemId) {
        return itemStorage.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .map(ItemMapper.INSTANCE::toItemDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundDataException("Item с таким id не найден"));
    }

    public List<ItemDto> getItemsByUser(Integer userId) {
        return itemStorage.getItems().stream()
                .filter(i -> i.getOwner().equals(userId))
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItems().stream()
                .filter(i -> i.getAvailable() &&
                        (i.getName().toLowerCase().contains(text.toLowerCase()) || i.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }
}
