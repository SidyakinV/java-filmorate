package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

@Component("memGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenre(Integer id) {
        return null;
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        return null;
    }

    @Override
    public Map<Long, List<Genre>> getCommonFilmGenres(Long filmId) {
        return null;
    }

}
