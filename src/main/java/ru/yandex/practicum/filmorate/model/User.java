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

    public void validate() {
    }

    public User copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), User.class);
    }

}
