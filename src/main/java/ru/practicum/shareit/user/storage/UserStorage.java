package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorage implements IUserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 1;

    public User addUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(Integer id) {
        users.remove(id);
    }
}