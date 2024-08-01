package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.PostValidationGroup;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.validator.ValidAvailable;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private Integer id;
    @NotBlank(groups = PostValidationGroup.class)
    private String name;
    @NotBlank(groups = PostValidationGroup.class)
    private String description;
    @ValidAvailable(groups = PostValidationGroup.class)
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
    private BookingOutDto lastBooking;
    private BookingOutDto nextBooking;
    private List<CommentDto> comments;
}
