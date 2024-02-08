package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.FilmsManager;
import ru.yandex.practicum.filmorate.managers.memory.InMemoryFilmsManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmsManager films;

    public FilmController() {
        films = new InMemoryFilmsManager();
    }

    // Получение списка всех фильмов
    @GetMapping
    public List<Film> listFilms() {
        return films.getFilmsList();
    }

    // Добавление нового фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            log.debug("Запрос на добавление нового фильма: {}", film);
            return films.addFilm(film);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Обновление существующего фильма по заданному id
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            log.debug("Запрос на изменение фильма: {}", film);
            return films.updateFilm(film);
        } catch (NotFoundException | ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
