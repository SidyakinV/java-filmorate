package ru.yandex.practicum.filmorate.model;

import com.google.gson.Gson;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    /*
    Валидация:
    - электронная почта не может быть пустой и должна содержать символ @;
    - логин не может быть пустым и содержать пробелы;
    - имя для отображения может быть пустым — в таком случае будет использован логин;
    - дата рождения не может быть в будущем.
    */
    public void validate() throws ValidationException {
        if ((email == null) || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Некорректный адрес электронной почты");
        }
        if ((login == null) || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Некорректный логин пользователя");
        }
        if ((birthday == null) || birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
        if ((name == null) || name.isBlank()) {
            name = login;
        }
    }

    public User copy() {
        Gson gson = new Gson();
        return gson.fromJson( gson.toJson(this), User.class );
    }

}
