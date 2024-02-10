package ru.yandex.practicum.filmorate.model;

import com.google.gson.Gson;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public Film copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), Film.class);
    }

}
