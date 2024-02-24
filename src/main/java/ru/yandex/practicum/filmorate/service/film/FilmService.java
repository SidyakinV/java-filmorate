package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    private FilmStorage filmStorage;

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
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) throws NotFoundException {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilmsList();
    }

    public void addUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException {
        Film film = filmStorage.getFilm(filmId);
        film.getUserLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void deleteUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException {
        Film film = filmStorage.getFilm(filmId);
        film.getUserLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopular(Integer count) {
        return
                filmStorage.getFilmsList().stream()
                        .sorted((f1, f2) -> Integer.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                        .limit(count)
                        .collect(Collectors.toList());
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
