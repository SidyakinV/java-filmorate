package ru.yandex.practicum.filmorate.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.memory.InMemoryUsersManager;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UsersManagersTest {

    private final InMemoryUsersManager userManager;

    public UsersManagersTest() {
        userManager = new InMemoryUsersManager();
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
        assertEquals(oldUser.getEmail(), newUser.getEmail());
        assertEquals(oldUser.getBirthday(), newUser.getBirthday());

        if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            assertEquals(oldUser.getLogin(), newUser.getName());
        } else {
            assertEquals(oldUser.getName(), newUser.getName());
        }
    }

    @Test
    public void addUser_addToList() {
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
    public void updUser_updList() {
        Long userId;
        try {
            userId = userManager.addUser(newDefaultUser()).getId();
        } catch (ValidationException e) {
            throw new RuntimeException("Ошибка при изменении пользователя: " + e.getMessage());
        }

        User user = newDefaultUser();
        user.setId(userId);
        user.setLogin("UserLogin");
        user.setName("Вася Пупкин");
        user.setEmail("pupkin@email.com");
        user.setBirthday(LocalDate.of(1999, 12, 31));

        User updUser;
        try {
            updUser = userManager.updateUser(user);
        } catch (ValidationException | NotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        checkEqualsUsers(user, updUser);

        User savedUser = userManager.getUser(userId);
        checkEqualsUsers(user, savedUser);
    }

    // электронная почта не может быть пустой и должна содержать символ @;
    @Test
    public void validateUser_throw_badEmail() {
        User user = newDefaultUser();

        user.setEmail(null);
        assertThrows(ValidationException.class, user::validate);

        user.setEmail(" ");
        assertThrows(ValidationException.class, user::validate);

        user.setEmail("user");
        assertThrows(ValidationException.class, user::validate);
    }

    // логин не может быть пустым и содержать пробелы;
    @Test
    public void validateUser_throw_badLogin() {
        User user = newDefaultUser();

        user.setLogin(null);
        assertThrows(ValidationException.class, user::validate);

        user.setLogin("");
        assertThrows(ValidationException.class, user::validate);

        user.setLogin("User Login");
        assertThrows(ValidationException.class, user::validate);
    }

    // дата рождения не может быть в будущем
    @Test
    public void validateUser_throw_badBirthday() {
        User user = newDefaultUser();

        user.setBirthday(null);
        assertThrows(ValidationException.class, user::validate);

        user.setBirthday(LocalDate.of(2100, 1, 1));
        assertThrows(ValidationException.class, user::validate);
    }

}
