package ru.yandex.practicum.filmorate.managers.memory;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.managers.FilmsManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InMemoryFilmsManager implements FilmsManager {

    private Long lastId;
    private final Map<Long, Film> filmsList;

    public InMemoryFilmsManager() {
        lastId = 0L;
        filmsList = new HashMap<>();
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsList.values());
    }

    @Override
    public Film addFilm(Film film) {
        Film newFilm = film.copy();
        newFilm.setId(++lastId);
        filmsList.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        if (!filmsList.containsKey(film.getId())) {
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", film.getId()));
        }

        Film newFilm = film.copy();
        filmsList.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film getFilm(Long id) {
        Film film = filmsList.get(id);
        return (film != null) ? film.copy() : null;
    }

    @Override
    public void clear() {
        filmsList.clear();
    }
}
