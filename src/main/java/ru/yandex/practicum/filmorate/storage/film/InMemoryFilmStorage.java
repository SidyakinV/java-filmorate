package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("memFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private Long lastId = 0L;
    private final Map<Long, Film> filmsList = new HashMap<>();

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsList.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(++lastId);
        filmsList.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmsList.containsKey(film.getId())) {
            return null;
        }
        filmsList.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        return filmsList.get(id);
    }

    @Override
    public void addUserLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        film.getUserLikes().add(userId);
        updateFilm(film);
    }

    @Override
    public void deleteUserLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        film.getUserLikes().remove(userId);
        updateFilm(film);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return
                getFilmsList().stream()
                        .sorted((f1, f2) -> Integer.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                        .limit(count)
                        .collect(Collectors.toList());
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        return null;
    }

}
