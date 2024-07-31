package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface IItemStorage {
    Item addItem(Item item);

    List<Item> getItems();
}
