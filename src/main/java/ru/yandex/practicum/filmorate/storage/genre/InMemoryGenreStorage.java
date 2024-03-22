package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@Slf4j
public class InMemoryGenreStorage implements GenreStorage {

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenre(Integer id) {
        return null;
    }

}
