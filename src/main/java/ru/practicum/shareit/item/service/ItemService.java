package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer ownerId);

    ItemDto getItemById(Integer id);

    List<ItemDto> getAllItemsByUserId(Integer userId);

    ItemDto updateItem(Integer ownerId, Integer id, ItemDto itemDto);

    void deleteItem(Integer id);

    List<ItemDto> searchItems(String text);
}
