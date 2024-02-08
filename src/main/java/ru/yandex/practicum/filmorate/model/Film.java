package ru.yandex.practicum.filmorate.model;

import com.google.gson.Gson;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    /*
    Валидация:
    - название не может быть пустым;
    - максимальная длина описания — 200 символов;
    - дата релиза — не раньше 28 декабря 1895 года;
    - продолжительность фильма должна быть положительной.
    */
    public void validate() throws ValidationException {
        if ((name == null) || name.isBlank()) {
            throw new ValidationException("Не заполнено название фильма");
        }
        if ((description != null) && (description.length() > 200)) {
            throw new ValidationException("Слишком длинное описание");
        }
        if ((releaseDate == null) ||releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза");
        }
        if ((duration == null) || duration <= 0) {
            throw new ValidationException("Некорректная продолжительность фильма");
        }
    }

    public Film copy() {
        Gson gson = new Gson();
        return gson.fromJson( gson.toJson(this), Film.class );
    }

}
