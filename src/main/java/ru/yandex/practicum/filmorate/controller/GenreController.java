package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // Список всех жанров
    // GET /genres
    @GetMapping
    public List<Genre> getGenres() {
        log.debug("Запрос на получение списка жанров");
        return genreService.getGenres();
    }

    // Жанр по идентификатору
    // GET /genres/{id}
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Integer id) throws NotFoundException {
        log.debug("Запрос на получение информации о жанре (ID: {})", id);
        return genreService.getGenre(id);
    }

}
