package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String email;
}