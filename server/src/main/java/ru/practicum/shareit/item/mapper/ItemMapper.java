package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request", target = "requestId")
    ItemDto toDto(Item item);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "requestId", target = "request")
    Item toEntity(ItemDto itemDto);
}
