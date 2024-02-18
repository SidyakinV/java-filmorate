package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.FilmsManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    @Autowired
    private FilmsManager filmsManager;

    // Получение списка всех фильмов
    @GetMapping
    public List<Film> listFilms() {
        return filmsManager.getFilmsList();
    }

    // Добавление нового фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            log.debug("Запрос на добавление нового фильма: {}", film);
            validate(film);
            return filmsManager.addFilm(film);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Обновление существующего фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            log.debug("Запрос на изменение фильма: {}", film);
            validate(film);
            return filmsManager.updateFilm(film);
        } catch (NotFoundException | ValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /*
    Валидация:
    - название не может быть пустым;
    - максимальная длина описания — 200 символов;
    - дата релиза — не раньше 28 декабря 1895 года;
    - продолжительность фильма должна быть положительной.
    */
    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Не заполнено название фильма");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Слишком длинное описание");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Некорректная продолжительность фильма");
        }
    }

}
