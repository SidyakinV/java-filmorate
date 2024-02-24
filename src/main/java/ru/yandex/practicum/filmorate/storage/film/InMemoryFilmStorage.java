package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long lastId;
    private final Map<Long, Film> filmsList;

    public InMemoryFilmStorage() {
        lastId = 0L;
        filmsList = new HashMap<>();
    }

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
    public Film updateFilm(Film film) throws NotFoundException {
        if (!filmsList.containsKey(film.getId())) {
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", film.getId()));
        }

        filmsList.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) throws NotFoundException {
        Film film = filmsList.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", id));
        }
        return film;
    }

}
