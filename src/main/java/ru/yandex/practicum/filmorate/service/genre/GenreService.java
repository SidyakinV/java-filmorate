package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getGenres() {
        log.debug("Запрос на получение списка жанров");
        return genreStorage.getGenres();
    }

    public Genre getGenre(Integer id) throws NotFoundException {
        log.debug("Запрос на получение информации о жанре (ID: {})", id);
        Genre genre = genreStorage.getGenre(id);
        if (genre == null) {
            throw new NotFoundException(String.format("Жанр с указанным ID %d не найден в базе данных", id));
        }
        return genre;
    }

}
