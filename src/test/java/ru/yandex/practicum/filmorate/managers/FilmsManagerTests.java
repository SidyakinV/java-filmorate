package ru.yandex.practicum.filmorate.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.managers.memory.InMemoryFilmsManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmsManagerTests {

    private final InMemoryFilmsManager filmsManager;

    public FilmsManagerTests() {
        filmsManager = new InMemoryFilmsManager();
    }

    private Film newDefaultFilm() {
        Film film = new Film();
        film.setName("Film Name");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(120);
        return film;
    }

    private void checkEqualsFilms(Film oldFilm, Film newFilm) {
        assertNotNull(newFilm);
        assertEquals(oldFilm.getName(), newFilm.getName());
        assertEquals(
                (oldFilm.getDescription() != null) ? oldFilm.getDescription() : "",
                (newFilm.getDescription() != null) ? newFilm.getDescription() : "");
        assertEquals(oldFilm.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(oldFilm.getDuration(), newFilm.getDuration());
    }

    @Test
    public void addFilm_addToList() {
        Film film = newDefaultFilm();

        Film newFilm;
        newFilm = filmsManager.addFilm(film);
        checkEqualsFilms(film, newFilm);

        Film savedFilm = filmsManager.getFilm(newFilm.getId());
        checkEqualsFilms(film, savedFilm);
    }

    @Test
    public void updateFilm_updateList() {
        Long filmId;
        filmId = filmsManager.addFilm(newDefaultFilm()).getId();

        Film film = newDefaultFilm();
        film.setId(filmId);
        film.setName("Просто хороший фильм");
        film.setDescription("Описание к хорошему фильму");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2024, 1, 1));

        Film updFilm;
        try {
            updFilm = filmsManager.updateFilm(film);
        } catch (NotFoundException e) {
            throw new RuntimeException("Ошибка при изменении фильма: " + e.getMessage());
        }
        checkEqualsFilms(film, updFilm);

        Film savedFilm = filmsManager.getFilm(filmId);
        checkEqualsFilms(film, savedFilm);
    }

}
