package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    private User newDefaultUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.of(2001, 1, 1));
        return user;
    }

    // электронная почта не может быть пустой и должна содержать символ @;
    @Test
    public void validateUser_throw_badEmail() {
        User user = newDefaultUser();

        user.setEmail(null);
        assertThrows(ValidationException.class, () -> UserController.validate(user));

        user.setEmail(" ");
        assertThrows(ValidationException.class, () -> UserController.validate(user));

        user.setEmail("user");
        assertThrows(ValidationException.class, () -> UserController.validate(user));
    }

    // логин не может быть пустым и содержать пробелы;
    @Test
    public void validateUser_throw_badLogin() {
        User user = newDefaultUser();

        user.setLogin(null);
        assertThrows(ValidationException.class, () -> UserController.validate(user));

        user.setLogin("");
        assertThrows(ValidationException.class, () -> UserController.validate(user));

        user.setLogin("User Login");
        assertThrows(ValidationException.class, () -> UserController.validate(user));
    }

    // дата рождения не может быть в будущем
    @Test
    public void validateUser_throw_badBirthday() {
        User user = newDefaultUser();

        user.setBirthday(null);
        assertThrows(ValidationException.class, () -> UserController.validate(user));

        user.setBirthday(LocalDate.of(2100, 1, 1));
        assertThrows(ValidationException.class, () -> UserController.validate(user));
    }

}
