package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request", target = "requestId")
    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "id")
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "requestId", target = "request")
    Item toEntity(ItemDto itemDto);
}
