package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Создание нового пользователя
    // POST /users
    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        log.debug("Запрос на создание нового пользователя: {}", user);
        return userService.addUser(user);
    }

    // Обновление существующего пользователя
    // PUT /users
    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, NotFoundException {
        log.debug("Запрос на изменение пользователя: {}", user);
        return userService.updateUser(user);
    }

    // Получение информации о пользователе
    // GET /users/{id}
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws NotFoundException {
        log.debug("Запрос на получение информации о пользователе (ID: {}) ", id);
        return userService.getUser(id);
    }

    // Получение списка всех пользователей
    // GET /users
    @GetMapping
    public List<User> getUsers() {
        log.debug("Запрос на получение списка пользователей");
        return userService.getUsers();
    }

    // Добавление в друзья
    // PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) throws NotFoundException {
        log.debug("Запрос на добавление в друзья: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    // DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) throws NotFoundException {
        log.debug("Запрос на удаление из друзей: userId={}, friendId={}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    // Возвращаем список пользователей, являющихся его друзьями
    // GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) throws NotFoundException {
        log.debug("Запрос на получение списка друзей пользователя: userId={}", id);
        return userService.getFriends(id);
    }

    // Список друзей, общих с другим пользователем
    // GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) throws NotFoundException {
        log.debug("Запрос на получение списка общих друзей: userId={}, otherId={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

}
