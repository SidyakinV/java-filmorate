package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsersList();

    User addUser(User user) throws ValidationException;

    User updateUser(User user) throws NotFoundException, ValidationException;

    User getUser(Long id);

}
