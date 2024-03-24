package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("dbGenreStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM genre");

        while (genreRows.next()) {
            genres.add(genreFromRowSet(genreRows));
        }

        return genres;
    }

    @Override
    public Genre getGenre(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM genre WHERE id = ?", id);

        if (!genreRows.next()) {
            log.info("Жанр с указанным ID {} не найден в базе данных", id);
            return null;
        }

        return genreFromRowSet(genreRows);
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        List<Genre> genres = new ArrayList<>();

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(
                "SELECT genre.* \n" +
                        "FROM film_genre \n" +
                        "INNER JOIN genre ON film_genre.genre_id = genre.id \n" +
                        "WHERE film_genre.film_id = ?", filmId);

        while (genresRows.next()) {
            genres.add(genreFromRowSet(genresRows));
        }

        return genres;
    }

    @Override
    public Map<Long, List<Genre>> getCommonFilmGenres(Long filmId) {
        Map<Long, List<Genre>> filmGenres = new HashMap<>();

        StringBuilder cmd = new StringBuilder(
                "SELECT film_genre.film_id, genre.* \n" +
                        "FROM film_genre \n" +
                        "  INNER JOIN genre ON film_genre.genre_id = genre.id \n"
        );
        if (filmId != 0) {
            cmd.append("WHERE film_genre.film_id = ").append(filmId);
        }

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(cmd.toString());
        while (genresRows.next()) {
            Long id = genresRows.getLong("film_id");

            if (!filmGenres.containsKey(id)) {
                filmGenres.put(id, new ArrayList<>());
            }

            Genre genre = new Genre();
            genre.setId(genresRows.getInt("id"));
            genre.setName(genresRows.getString("name"));
            filmGenres.get(id).add(genre);
        }

        return filmGenres;
    }

    private Genre genreFromRowSet(SqlRowSet genreRow) {
        Genre genre = new Genre();
        genre.setId(genreRow.getInt("id"));
        genre.setName(genreRow.getString("name"));
        return genre;
    }

}
