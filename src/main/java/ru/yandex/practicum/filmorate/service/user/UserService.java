package ru.yandex.practicum.filmorate.service.user;

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
public class UserService {

    @Autowired
    private UserStorage userStorage;

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
        validate(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException, NotFoundException {
        validate(user);
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) throws NotFoundException {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsersList();
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) throws NotFoundException {
        User user = userStorage.getUser(userId);
        List<User> users = new ArrayList<>();
        for (Long id : user.getFriends()) {
            try {
                users.add(userStorage.getUser(id));
            } catch (NotFoundException ignored) {
            }
        }
        return users;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException {
        Set<Long> friends1 = userStorage.getUser(userId).getFriends();
        Set<Long> friends2 = userStorage.getUser(otherId).getFriends();
        List<User> list = new ArrayList<>();
        for (Long id : friends1) {
            if (friends2.contains(id)) {
                try {
                    list.add(userStorage.getUser(id));
                } catch (NotFoundException ignored) {
                }
            }
        }
        return list;
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
