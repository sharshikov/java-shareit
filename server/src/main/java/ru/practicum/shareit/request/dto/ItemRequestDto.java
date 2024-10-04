package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;          // Идентификатор запроса
    private String description;  // Описание запроса
    private String created;      // Дата создания в виде строки
    private Integer userId;      // Идентификатор пользователя, создавшего запрос
    private List<ItemDto> items;
}
