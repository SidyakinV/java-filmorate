package ru.yandex.practicum.filmorate.managers.memory;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.UsersManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUsersManager implements UsersManager {

    private Long lastId;
    final Map<Long, User> usersList;

    public InMemoryUsersManager() {
        lastId = 0L;
        usersList = new HashMap<>();
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(usersList.values());
    }

    @Override
    public User addUser(User user) throws ValidationException {
        User newUser = user.copy();
        newUser.validate();
        newUser.setId(++lastId);
        usersList.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ValidationException {
        if (!usersList.containsKey(user.getId())) {
            throw new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", user.getId()));
        }

        User newUser = user.copy();
        newUser.validate();
        usersList.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User getUser(Long id) {
        User user = usersList.get(id);
        return (user != null) ? user.copy() : null;
    }

    @Override
    public void clear() {
        usersList.clear();
    }
}
