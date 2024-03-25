package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    @Autowired
    @Qualifier("dbFilmStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("dbUserStorage")
    private UserStorage userStorage;

    @Autowired
    @Qualifier("dbMpaStorage")
    private MpaStorage mpaStorage;

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

        Film dbFilm = filmStorage.updateFilm(film);
        if (dbFilm == null) {
            log.info("Фильм с указанным ID {} не найден в базе данных", film.getId());
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", film.getId()));
        }
        return dbFilm;
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

    public void addUserLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Запрос на установку лайка фильму: filmId={}, userId={}", filmId, userId);
        if (filmStorage.getFilm(filmId) == null) {
            log.info("Фильм с указанным ID {} не найден в базе данных", filmId);
            throw new ValidationException(String.format("Фильм с указанным ID %d не найден в базе данных", filmId));
        }
        if (userStorage.getUser(userId) == null) {
            log.info("Пользователь с указанным ID {} не найден в базе данных", userId);
            throw new ValidationException(String.format("Пользователь с указанным ID %d не найден в базе данных", userId));
        }
        filmStorage.addUserLike(filmId, userId);
    }

    public void deleteUserLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Запрос на снятие лайка фильму: filmId={}, userId={}", filmId, userId);
        if (filmStorage.getFilm(filmId) == null) {
            log.info("Фильм с указанным ID {} не найден в базе данных", filmId);
            throw new ValidationException(String.format("Фильм с указанным ID %d не найден в базе данных", filmId));
        }
        if (userStorage.getUser(userId) == null) {
            log.info("Пользователь с указанным ID {} не найден в базе данных", userId);
            throw new ValidationException(String.format("Пользователь с указанным ID %d не найден в базе данных", userId));
        }
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
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            int mpaId = film.getMpa().getId();
            if (mpaStorage.getMpa(mpaId) == null) {
                throw new ValidationException(String.format("MPA-рейтинг с указанным ID %d не найден в базе данных", mpaId));
            }
        }

    }

}
