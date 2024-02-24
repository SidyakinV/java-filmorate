package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@SuppressWarnings("unused")
public class FilmController {

    @SuppressWarnings("FieldCanBeLocal")
    private final int POPULAR_DEFAULT = 10;

    @Autowired
    private FilmService filmService;

    // Добавление нового фильма
    // POST /films
    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        log.debug("Запрос на добавление нового фильма: {}", film);
        return filmService.addFilm(film);
    }

    // Обновление существующего фильма
    // PUT /films
    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        log.debug("Запрос на изменение фильма: {}", film);
        return filmService.updateFilm(film);
    }

    // Получение информации о фильме
    // GET /films/{id}
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) throws NotFoundException {
        log.debug("Запрос на получение информации о фильме (ID: {})", id);
        return filmService.getFilm(id);
    }

    // Получение списка всех фильмов
    // GET /films
    @GetMapping
    public List<Film> getFilms() {
        log.debug("Запрос на получение списка фильмов");
        return filmService.getFilms();
    }

    // Пользователь ставит лайк фильму
    // PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) throws NotFoundException, ValidationException {
        log.debug("Запрос на установку лайка фильму: filmId={}, userId={}", id, userId);
        filmService.addUserLike(id, userId);
    }

    // Пользователь удаляет лайк
    // DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) throws NotFoundException, ValidationException {
        log.debug("Запрос на снятие лайка фильму: filmId={}, userId={}", id, userId);
        filmService.deleteUserLike(id, userId);
    }

    // Возвращает список из первых count фильмов по количеству лайков
    // Если значение параметра count не задано, верните первые 10
    // GET /films/popular?count={count}
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
        log.debug("Запрос на получение списка популярных фильмов: count={}", count == null ?
                "default (" + POPULAR_DEFAULT + ")" : count);
        return filmService.getPopular(count == null ? POPULAR_DEFAULT : count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final ValidationException e) {
        log.error(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.error(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Exception e) {
        log.error(e.getMessage());
        return Map.of("error", e.getMessage());
    }

}
