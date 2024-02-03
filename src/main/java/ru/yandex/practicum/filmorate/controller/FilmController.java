package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
public class FilmController {
    private int id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private Duration duration;
}
