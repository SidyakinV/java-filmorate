package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UsersManagersTest {

    private final InMemoryUserStorage userManager;

    public UsersManagersTest() {
        userManager = new InMemoryUserStorage();
    }

    private User newDefaultUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.of(2001, 1, 1));
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
            newUser = userManager.addUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении нового пользователя: " + e.getMessage());
        }
        checkEqualsUsers(user, newUser);

        User savedUser = userManager.getUser(newUser.getId());
        checkEqualsUsers(user, savedUser);
    }

    @Test
    public void updateUser_updateList() throws NotFoundException {
        Long userId;
        userId = userManager.addUser(newDefaultUser()).getId();

        User user = newDefaultUser();
        user.setId(userId);
        user.setLogin("UserLogin");
        user.setName("Вася Пупкин");
        user.setEmail("pupkin@email.com");
        user.setBirthday(LocalDate.of(1999, 12, 31));

        User updUser;
        try {
            updUser = userManager.updateUser(user);
        } catch (NotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        checkEqualsUsers(user, updUser);

        User savedUser = userManager.getUser(userId);
        checkEqualsUsers(user, savedUser);
    }

}
