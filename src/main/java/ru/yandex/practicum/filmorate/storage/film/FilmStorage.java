package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilmsList();

    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws NotFoundException, ValidationException;

    Film getFilm(Long id);

    void addUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException;

    void deleteUserLike(Long filmId, Long userId) throws NotFoundException, ValidationException;

    List<Film> getPopular(Integer count);

}
