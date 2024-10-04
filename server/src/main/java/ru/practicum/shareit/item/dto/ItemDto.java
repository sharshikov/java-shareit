package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
    private BookingOutDto lastBooking;
    private BookingOutDto nextBooking;
    private List<CommentDto> comments;
}
