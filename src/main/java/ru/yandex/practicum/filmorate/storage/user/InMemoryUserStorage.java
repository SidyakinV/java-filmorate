package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long lastId;
    final Map<Long, User> usersList;

    public InMemoryUserStorage() {
        lastId = 0L;
        usersList = new HashMap<>();
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(usersList.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(++lastId);
        usersList.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        if (!usersList.containsKey(user.getId())) {
            throw new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", user.getId()));
        }

        usersList.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return usersList.get(id);
    }

}
