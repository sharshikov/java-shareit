package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class ItemForRequest {
    private Integer id;
    private String name;
    private Integer ownerId;
}