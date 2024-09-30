package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.PostValidationGroup;

@Getter
@Setter
public class UserDto {
    private Integer id;
    @NotBlank(groups = PostValidationGroup.class)
    private String name;
    @NotBlank(groups = PostValidationGroup.class)
    @Email
    private String email;
}
