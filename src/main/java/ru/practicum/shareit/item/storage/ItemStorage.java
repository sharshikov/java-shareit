package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorage implements IItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private static int id = 1;

    public Item addItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }
}