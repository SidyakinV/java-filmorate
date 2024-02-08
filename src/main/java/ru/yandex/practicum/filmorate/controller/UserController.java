package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.UsersManager;
import ru.yandex.practicum.filmorate.managers.memory.InMemoryUsersManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UsersManager users;

    public UserController() {
        users = new InMemoryUsersManager();
    }

    // Получение списка всех пользователей
    @GetMapping
    public List<User> listUsers() {
        return users.getUsersList();
    }

    // Создание нового пользователя
    @PostMapping
    public User addUser(@RequestBody User user) {
        try {
            log.debug("Запрос на создание нового пользователя: {}", user);
            return users.addUser(user);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Обновление существующего пользователя по заданному id
    @PutMapping()
    public User updateUser(@RequestBody User user) {
        try {
            log.debug("Запрос на изменение пользователя: {}", user);
            return users.updateUser(user);
        } catch (NotFoundException | ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
