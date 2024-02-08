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
        if (email.isBlank()) {
            throw new ValidationException("Некорректный адрес электронной почты");
        }
    }

    public User copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), User.class);
    }

}
