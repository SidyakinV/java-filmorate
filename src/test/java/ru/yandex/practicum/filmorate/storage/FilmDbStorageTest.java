package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void addFilmTest() {
        FilmStorage filmStorage = newFilmStorage();

        Film newFilm = createDefaultFilm();
        Film film = filmStorage.addFilm(newFilm);
        newFilm.setId(film.getId());

        Film savedFilm = filmStorage.getFilm(film.getId());

        compare(newFilm, savedFilm);
    }

    @Test
    public void updateFilmTest() {
        FilmStorage filmStorage = newFilmStorage();

        Film newFilm = createDefaultFilm();
        Film film = filmStorage.addFilm(newFilm);

        film.setName("Просто фильм");
        film.setDescription("Простое описание");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(111);
        filmStorage.updateFilm(film);

        Film savedFilm = filmStorage.getFilm(film.getId());

        compare(film, savedFilm);
    }

    @Test
    public void addLikeTest() throws ValidationException {

        FilmStorage filmStorage = newFilmStorage();
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);

        Film film = createDefaultFilm();
        User user = UserDbStorageTest.createDefaultUser();

        film = filmStorage.addFilm(film);
        user = userStorage.addUser(user);

        filmStorage.addUserLike(film.getId(), user.getId());
        Set<Long> likes = filmStorage.getLikes(film.getId());

        assertEquals(1, likes.size());
        assertTrue(likes.contains(user.getId()));

    }

    @Test
    public void removeLikeTest() throws ValidationException {

        FilmStorage filmStorage = newFilmStorage();
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);

        Film film = createDefaultFilm();
        User user = UserDbStorageTest.createDefaultUser();

        film = filmStorage.addFilm(film);
        user = userStorage.addUser(user);

        filmStorage.addUserLike(film.getId(), user.getId());
        Set<Long> likes = filmStorage.getLikes(film.getId());
        assertEquals(1, likes.size());

        filmStorage.deleteUserLike(film.getId(), user.getId());
        likes = filmStorage.getLikes(film.getId());
        assertEquals(0, likes.size());

    }

    private Film createDefaultFilm() {
        Film film = new Film();
        film.setName("Самый лучший фильм");
        film.setDescription("Описание к лучшему фильму");
        film.setReleaseDate(LocalDate.of(1991, 12, 31));
        film.setDuration(100);
        return film;
    }

    private void compare(Film film1, Film film2) {
        assertNotNull(film1);
        assertNotNull(film2);
        assertEquals(film1.getId(), film2.getId());
        assertEquals(film1.getName(), film2.getName());
        assertEquals(film1.getDescription(), film2.getDescription());
        assertEquals(film1.getDuration(), film2.getDuration());
        assertEquals(film1.getReleaseDate(), film2.getReleaseDate());
    }

    private FilmStorage newFilmStorage() {
        return new FilmDbStorage(jdbcTemplate);
    }

}
