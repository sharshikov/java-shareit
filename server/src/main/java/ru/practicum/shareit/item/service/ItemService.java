package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer ownerId);

    ItemDto getItemById(Integer id);
}
