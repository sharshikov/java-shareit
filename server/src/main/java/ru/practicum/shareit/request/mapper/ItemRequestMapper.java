package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(source = "user.id", target = "userId")   // Преобразование поля user.id в userId
    @Mapping(source = "created", target = "created")
    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(source = "userId", target = "user.id")   // Преобразование поля userId в user.id
    @Mapping(source = "created", target = "created")
    ItemRequest toEntity(ItemRequestDto itemRequestDto);
}
