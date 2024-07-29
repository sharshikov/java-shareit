package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface IUserStorage {
    User addUser(User user);

    List<User> getUsers();

    void deleteUser(Integer id);
}
