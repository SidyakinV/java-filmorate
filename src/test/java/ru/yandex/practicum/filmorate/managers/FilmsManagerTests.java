package ru.yandex.practicum.filmorate.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.managers.memory.InMemoryFilmsManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

public class FilmsManagerTests {

    private final InMemoryFilmsManager filmsManager;

    public FilmsManagerTests() {
        filmsManager = new InMemoryFilmsManager();
    }

    private Film newDefaultFilm() {
        Film film = new Film();
        film.setName("Film Name");
        film.setReleaseDate( LocalDate.of(2001,1,1) );
        film.setDuration(120);
        return film;
    }

    private void checkEqualsFilms(Film oldFilm, Film newFilm) {
        assertNotNull(newFilm);
        assertEquals(oldFilm.getName(), newFilm.getName());
        assertEquals(
                (oldFilm.getDescription() != null) ? oldFilm.getDescription() : "",
                (newFilm.getDescription() != null) ? newFilm.getDescription() : "" );
        assertEquals(oldFilm.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(oldFilm.getDuration(), newFilm.getDuration());
    }

    @Test
    public void addFilm_addToList() {
        Film film = newDefaultFilm();

        Film newFilm;
        try {
            newFilm = filmsManager.addFilm(film);
        } catch (ValidationException e) {
            throw new RuntimeException("Ошибка при добавлении фильма: " + e.getMessage());
        }
        checkEqualsFilms(film, newFilm);

        Film savedFilm = filmsManager.getFilm(newFilm.getId());
        checkEqualsFilms(film, savedFilm);
    }

    @Test
    public void updFilm_updList() {
        Long filmId;
        try {
            filmId = filmsManager.addFilm( newDefaultFilm() ).getId();
        } catch (ValidationException e) {
            throw new RuntimeException("Ошибка при изменении фильма: " + e.getMessage());
        }

        Film film = newDefaultFilm();
        film.setId(filmId);
        film.setName("Просто хороший фильм");
        film.setDescription("Описание к хорошему фильму");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2024,1,1));

        Film updFilm;
        try {
            updFilm = filmsManager.updateFilm(film);
        } catch (NotFoundException | ValidationException e) {
            throw new RuntimeException("Ошибка при изменении фильма: " + e.getMessage());
        }
        checkEqualsFilms(film, updFilm);

        Film savedFilm = filmsManager.getFilm(filmId);
        checkEqualsFilms(film, savedFilm);
    }

    // название не может быть пустым
    @Test
    public void validateFilm_throw_badName() {
        Film film = newDefaultFilm();

        film.setName(null);
        assertThrows(ValidationException.class, film::validate);

        film.setName("  ");
        assertThrows(ValidationException.class, film::validate);
    }

    // максимальная длина описания — 200 символов
    @Test
    public void validateFilm_throw_badDescription() {
        Film film = newDefaultFilm();

        film.setDescription( "*".repeat(201) );
        assertThrows(ValidationException.class, film::validate);
    }

    @Test
    public void validateFilm_rightDescription() {
        Film film = newDefaultFilm();

        film.setDescription(null);
        assertDoesNotThrow(film::validate);

        film.setDescription(" ");
        assertDoesNotThrow(film::validate);

        film.setDescription("*");
        assertDoesNotThrow(film::validate);

        film.setDescription( "*".repeat(200) );
        assertDoesNotThrow(film::validate);
    }

    // дата релиза — не раньше 28 декабря 1895 года
    @Test
    public void validateFilm_throw_badReleaseDate() {
        Film film = newDefaultFilm();

        film.setReleaseDate(null);
        assertThrows(ValidationException.class, film::validate);

        film.setReleaseDate( LocalDate.of(1895, 12, 27) );
        assertThrows(ValidationException.class, film::validate);
    }

    @Test void validateFilm_rightReleaseDate() {
        Film film = newDefaultFilm();

        film.setReleaseDate( LocalDate.of(1895, 12, 28) );
        assertDoesNotThrow(film::validate);
    }

    // продолжительность фильма должна быть положительной
    @Test
    public void validateFilm_throw_badDuration() {
        Film film = newDefaultFilm();

        film.setDuration(null);
        assertThrows(ValidationException.class, film::validate);

        film.setDuration(-1);
        assertThrows(ValidationException.class, film::validate);

        film.setDuration(0);
        assertThrows(ValidationException.class, film::validate);
    }

    @Test
    public void validateFilm_rightDuration() {
        Film film = newDefaultFilm();

        film.setDuration(1);
        assertDoesNotThrow(film::validate);
    }

}
