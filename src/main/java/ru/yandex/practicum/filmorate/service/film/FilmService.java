package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    private final FilmStorage filmStorage;

    /*
    Список операций:
      - добавление нового фильма;
      - обновление существующего фильма;
      - получение списка всех фильмов;
      - добавление и удаление лайка;
      - вывод 10 наиболее популярных фильмов по количеству лайков.
    Примечание:
      - Пусть пока каждый пользователь может поставить лайк фильму только один раз.
    */

    public Film addFilm(Film film) throws ValidationException {
        log.debug("Запрос на добавление нового фильма: {}", film);
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        log.debug("Запрос на изменение фильма: {}", film);
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) throws NotFoundException {
        log.debug("Запрос на получение информации о фильме (ID: {})", id);
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", id));
        }
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilmsList();
    }

    public void addUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException {
        log.debug("Запрос на установку лайка фильму: filmId={}, userId={}", filmId, userId);
        filmStorage.addUserLike(filmId, userId);
    }

    public void deleteUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException {
        log.debug("Запрос на снятие лайка фильму: filmId={}, userId={}", filmId, userId);
        filmStorage.deleteUserLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        log.debug("Запрос на получение списка популярных фильмов: count={}", count);
        return filmStorage.getPopular(count);
    }

    /*
    Валидация:
    - название не может быть пустым;
    - максимальная длина описания — 200 символов;
    - дата релиза — не раньше 28 декабря 1895 года;
    - продолжительность фильма должна быть положительной.
    */
    private void validate(Film film) throws ValidationException {
        log.debug("Валидация фильма: {}", film);
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
