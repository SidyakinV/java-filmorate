package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;

    /*
    Список операций:
      - создание пользователя;
      - обновление пользователя;
      - получение списка всех пользователей.
      - добавление в друзья;
      - удаление из друзей;
      - вывод списка общих друзей.
    Примечание:
      - Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
        То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
    */

    public User addUser(User user) throws ValidationException {
        log.debug("Запрос на создание нового пользователя: {}", user);
        validate(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException, NotFoundException {
        log.debug("Запрос на изменение пользователя: {}", user);
        validate(user);
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) throws NotFoundException {
        log.debug("Запрос на получение информации о пользователе (ID: {}) ", id);
        User user = userStorage.getUser(id);
        if (user == null) {
            throw  new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", id));
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsersList();
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        log.debug("Запрос на добавление в друзья: userId={}, friendId={}", userId, friendId);
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        log.debug("Запрос на удаление из друзей: userId={}, friendId={}", userId, friendId);
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) throws NotFoundException {
        log.debug("Запрос на получение списка друзей пользователя: userId={}", userId);
        User user = getUser(userId);
        List<User> users = new ArrayList<>();
        for (Long id : user.getFriends()) {
            try {
                users.add(getUser(id));
            } catch (NotFoundException ignored) {
            }
        }
        return users;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException {
        log.debug("Запрос на получение списка общих друзей: userId={}, otherId={}", userId, otherId);
        Set<Long> friends1 = getUser(userId).getFriends();
        Set<Long> friends2 = getUser(otherId).getFriends();
        List<User> list = new ArrayList<>();
        for (Long id : friends1) {
            if (friends2.contains(id)) {
                try {
                    list.add(getUser(id));
                } catch (NotFoundException ignored) {
                }
            }
        }
        return list;
    }

    private void validate(User user) throws ValidationException {
        log.debug("Ошибка валидации пользователя: {}", user);
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
