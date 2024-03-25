package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UsersManagersTest {

    private final InMemoryUserStorage userStorage;

    public UsersManagersTest() {
        userStorage = new InMemoryUserStorage();
    }

    private User newDefaultUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.of(2001, 1, 1));
        return user;
    }

    public User getUser(Long id) throws NotFoundException {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", id));
        }
        return user;
    }

    private void checkEqualsUsers(User oldUser, User newUser) {
        assertNotNull(newUser);
        assertEquals(oldUser.getLogin(), newUser.getLogin());
        assertEquals(oldUser.getName(), newUser.getName());
        assertEquals(oldUser.getEmail(), newUser.getEmail());
        assertEquals(oldUser.getBirthday(), newUser.getBirthday());
    }

    @Test
    public void addUser_addToList() throws NotFoundException {
        User user = newDefaultUser();

        User newUser;
        try {
            newUser = userStorage.addUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении нового пользователя: " + e.getMessage());
        }
        checkEqualsUsers(user, newUser);

        User savedUser = getUser(newUser.getId());
        checkEqualsUsers(user, savedUser);
    }

    @Test
    public void updateUser_updateList() throws NotFoundException {
        Long userId;
        userId = userStorage.addUser(newDefaultUser()).getId();

        User user = newDefaultUser();
        user.setId(userId);
        user.setLogin("UserLogin");
        user.setName("Вася Пупкин");
        user.setEmail("pupkin@email.com");
        user.setBirthday(LocalDate.of(1999, 12, 31));

        User updUser = userStorage.updateUser(user);
        if (updUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        checkEqualsUsers(user, updUser);

        User savedUser = getUser(userId);
        checkEqualsUsers(user, savedUser);
    }

}
