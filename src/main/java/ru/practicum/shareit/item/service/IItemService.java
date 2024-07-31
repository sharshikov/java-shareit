package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface IItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> getItemsByUser(Integer userId);

    List<ItemDto> searchItems(String text);
}
