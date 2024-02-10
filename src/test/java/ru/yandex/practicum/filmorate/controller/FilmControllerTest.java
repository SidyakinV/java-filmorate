package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    private Film newDefaultFilm() {
        Film film = new Film();
        film.setName("Film Name");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(120);
        return film;
    }

    // название не может быть пустым
    @Test
    public void validateFilm_throw_badName() {
        Film film = newDefaultFilm();

        film.setName(null);
        assertThrows(ValidationException.class, () -> FilmController.validate(film));

        film.setName("  ");
        assertThrows(ValidationException.class, () -> FilmController.validate(film));
    }

    // максимальная длина описания — 200 символов
    @Test
    public void validateFilm_throw_badDescription() {
        Film film = newDefaultFilm();

        film.setDescription("*".repeat(201));
        assertThrows(ValidationException.class, () -> FilmController.validate(film));
    }

    @Test
    public void validateFilm_rightDescription() {
        Film film = newDefaultFilm();

        film.setDescription(null);
        assertDoesNotThrow(() -> FilmController.validate(film));

        film.setDescription(" ");
        assertDoesNotThrow(() -> FilmController.validate(film));

        film.setDescription("*");
        assertDoesNotThrow(() -> FilmController.validate(film));

        film.setDescription("*".repeat(200));
        assertDoesNotThrow(() -> FilmController.validate(film));
    }

    // дата релиза — не раньше 28 декабря 1895 года
    @Test
    public void validateFilm_throw_badReleaseDate() {
        Film film = newDefaultFilm();

        film.setReleaseDate(null);
        assertThrows(ValidationException.class, () -> FilmController.validate(film));

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> FilmController.validate(film));
    }

    @Test
    void validateFilm_rightReleaseDate() {
        Film film = newDefaultFilm();

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> FilmController.validate(film));
    }

    // продолжительность фильма должна быть положительной
    @Test
    public void validateFilm_throw_badDuration() {
        Film film = newDefaultFilm();

        film.setDuration(null);
        assertThrows(ValidationException.class, () -> FilmController.validate(film));

        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> FilmController.validate(film));

        film.setDuration(0);
        assertThrows(ValidationException.class, () -> FilmController.validate(film));
    }

    @Test
    public void validateFilm_rightDuration() {
        Film film = newDefaultFilm();

        film.setDuration(1);
        assertDoesNotThrow(() -> FilmController.validate(film));
    }

}
