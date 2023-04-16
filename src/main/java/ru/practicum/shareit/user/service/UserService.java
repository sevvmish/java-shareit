package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(long userId, User user);

    User findById(long userId);

    void delete(long userId);

    List<User> getAll();
}
