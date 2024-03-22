package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilmsList() {
        List<Film> films = new ArrayList<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM film");

        while (filmRows.next()) {
            films.add(filmFromRowSet(filmRows));
        }

        return films;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {

        Optional<Integer> mpa = checkMpa(film.getMpa());

        String sqlQuery =
                "insert into film (name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().format(dateTimeFormatter));
            stmt.setString(4, String.valueOf(film.getDuration()));
            if (mpa.isEmpty()) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setString(5, String.valueOf(mpa.get()));
            }
            return stmt;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        film.setId(id);
        updateGenre(film);

        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException, ValidationException {

        Optional<Integer> mpa = checkMpa(film.getMpa());

        String sqlQuery =
                "update film set " +
                "  name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(dateTimeFormatter),
                film.getDuration(),
                mpa.orElse(null),
                film.getId());

        updateGenre(film);

        Long id = film.getId();
        film = getFilm(id);
        if (film == null) {
            log.info("Фильм с указанным ID {} не найден в базе данных", id);
            throw new NotFoundException(String.format("Фильм с указанным ID (%d) не найден", id));
        }

        return film;
    }

    @Override
    public Film getFilm(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM film WHERE id = ?", id);

        if(!filmRows.next()) {
            log.info("Фильм с указанным ID {} не найден в базе данных", id);
            return null;
        }

        return filmFromRowSet(filmRows);
    }

    @Override
    public void addUserLike(Long filmId, Long userId) throws ValidationException {
        checkFilmExists(filmId);
        checkUserExists(userId);

        String sqlQuery =
            "INSERT INTO user_film (film_id, user_id) \n " +
            "SELECT ?, ? \n " +
            "WHERE NOT EXISTS (SELECT * FROM user_film WHERE film_id = ? AND user_id = ?) ";
        jdbcTemplate.update(sqlQuery,
                filmId, userId, filmId, userId);
    }

    @Override
    public void deleteUserLike(Long filmId, Long userId) throws ValidationException {
        checkFilmExists(filmId);
        checkUserExists(userId);

        String sqlQuery =
                "DELETE FROM user_film " +
                "WHERE film_id = ? AND user_id = ? ";
        jdbcTemplate.update(sqlQuery,
                filmId, userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        List<Film> films = new ArrayList<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT \n" +
                    "  film.*, \n" +
                    "  (SELECT count(*) FROM user_film WHERE film.id = user_film.film_id) AS count_likes \n" +
                    "FROM film \n" +
                    "ORDER BY count_likes DESC, film.name \n" +
                    "LIMIT " + count);

        while (filmRows.next()) {
            films.add(filmFromRowSet(filmRows));
        }

        return films;
    }

    public Set<Long> getLikes(Long filmId) {
        Set<Long> likes = new HashSet<>();

        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(
                "SELECT * \n" +
                    "FROM user_film \n " +
                    "WHERE film_id = ? ",
                filmId);

        while (likeRows.next()) {
            likes.add(likeRows.getLong("user_id"));
        }

        return likes;
    }

    private Film filmFromRowSet(SqlRowSet filmRow) {
        Film film = new Film();

        film.setId(filmRow.getLong("id"));
        film.setName(filmRow.getString("name"));
        film.setDescription(filmRow.getString("description"));
        film.setReleaseDate(Objects.requireNonNull(filmRow.getDate("release_date")).toLocalDate());
        film.setDuration(filmRow.getInt("duration"));

        Integer rating = filmRow.getInt("rating_id");
        if (filmRow.wasNull()) {
            rating = null;
        }
        film.getMpa().setId(rating);

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(
                "SELECT genre_id FROM film_genre WHERE film_id = ?", film.getId());

        while (genresRows.next()) {
            Genre genre = new Genre();
            genre.setId(genresRows.getInt("genre_id"));
            film.getGenres().add(genre);
        }

        return film;
    }

    private void updateGenre(Film film) {
        String sqlQuery =
                "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        StringBuilder listId = new StringBuilder();
        for (Genre genre : film.getGenres()) {
            listId.append(",").append(genre.getId());
        }
        if (listId.length() > 0) {
            listId.delete(0, 1);
        }

        sqlQuery =
                "INSERT INTO film_genre (film_id, genre_id) \n" +
                "SELECT ?, id \n" +
                "FROM genre \n" +
                "WHERE id IN (" + listId + ") \n" +
                "AND id NOT IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        jdbcTemplate.update(sqlQuery, film.getId(), film.getId());
    }

    private Optional<Integer> checkMpa(Mpa mpa) throws ValidationException {
        if ((mpa == null) || (mpa.getId() == null)) {
            return Optional.empty();
        }

        Integer id = mpa.getId();

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM rating WHERE id = ?", id);

        if(!mpaRows.next()) {
            log.info("Код MPA {} отсутствует в списке", id);
            throw new ValidationException(String.format("Код MPA %d отсутствует в списке", id));
        }

        return Optional.of(id);
    }

    private void checkFilmExists(Long id) throws ValidationException {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM film WHERE id = ?", id);

        if(!filmRows.next()) {
            log.info("Фильм с указанным ID {} не найден в базе данных", id);
            throw new ValidationException(String.format("Фильм с указанным ID %d не найден в базе данных", id));
        }
    }

    private void checkUserExists(Long id) throws ValidationException {

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM `user` WHERE id = ?", id);
        if(!filmRows.next()) {
            log.info("Пользователь с указанным ID {} не найден в базе данных", id);
            throw new ValidationException(String.format("Пользователь с указанным ID %d не найден в базе данных", id));
        }
    }
}
