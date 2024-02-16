package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.UsersManager;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UsersManager usersManager;

    // Получение списка всех пользователей
    @GetMapping
    public List<User> listUsers() {
        return usersManager.getUsersList();
    }

    // Создание нового пользователя
    @PostMapping
    public User addUser(@RequestBody User user) {
        try {
            log.debug("Запрос на создание нового пользователя: {}", user);
            validate(user);
            return usersManager.addUser(user);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Обновление существующего пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        try {
            log.debug("Запрос на изменение пользователя: {}", user);
            validate(user);
            return usersManager.updateUser(user);
        } catch (NotFoundException | ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный адрес электронной почты");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин пользователя");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
