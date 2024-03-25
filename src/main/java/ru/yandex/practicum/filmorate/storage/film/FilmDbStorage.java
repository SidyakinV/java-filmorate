package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("dbFilmStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<Film> getFilmsList() {
        List<Film> films = new ArrayList<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT film.*, rating.name as mpa_name \n" +
                        "FROM film \n" +
                        "  LEFT JOIN rating on film.rating_id = rating.id "
        );

        while (filmRows.next()) {
            films.add(filmFromRowSet(filmRows));
        }

        setFilmGenres(films);

        return films;
    }

    @Override
    public Film addFilm(Film film) {

        Optional<Integer> mpaId = getMpaId(film.getMpa());

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
            if (mpaId.isEmpty()) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setString(5, String.valueOf(mpaId.get()));
            }
            return stmt;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // Вместе с фильмом передается список жанров, который также необходимо привести в соответствие
        film.setId(id);
        updateFilmGenre(film);

        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {

        Optional<Integer> mpaId = getMpaId(film.getMpa());

        String sqlQuery =
                "update film set " +
                        "  name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                        "where id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(dateTimeFormatter),
                film.getDuration(),
                mpaId.orElse(null),
                film.getId());

        // Также приводим в соответствие список жанров фильма
        updateFilmGenre(film);

        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT film.*, rating.name as mpa_name \n" +
                        "FROM film \n" +
                        "  LEFT JOIN rating on film.rating_id = rating.id \n" +
                        "WHERE film.id = ?",
                id);

        if (!filmRows.next()) {
            log.info("Фильм с указанным ID {} не найден в базе данных", id);
            return null;
        }

        Film film = filmFromRowSet(filmRows);
        setFilmGenres(film);

        return film;
    }

    @Override
    public void addUserLike(Long filmId, Long userId) {
        String sqlQuery =
                "INSERT INTO user_film (film_id, user_id) \n " +
                        "SELECT ?, ? \n " +
                        "WHERE NOT EXISTS (SELECT * FROM user_film WHERE film_id = ? AND user_id = ?) ";
        jdbcTemplate.update(sqlQuery,
                filmId, userId, filmId, userId);
    }

    @Override
    public void deleteUserLike(Long filmId, Long userId) {
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
                        "  film.*, rating.name as mpa_name, \n" +
                        "  (SELECT count(*) FROM user_film WHERE film.id = user_film.film_id) AS count_likes \n" +
                        "FROM film \n" +
                        "  LEFT JOIN rating on film.rating_id = rating.id \n" +
                        "ORDER BY count_likes DESC, film.name \n" +
                        "LIMIT " + count);

        while (filmRows.next()) {
            films.add(filmFromRowSet(filmRows));
        }

        setFilmGenres(films);

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

        int mpaId = filmRow.getInt("rating_id");
        if (!filmRow.wasNull()) {
            film.getMpa().setId(mpaId);
            film.getMpa().setName(filmRow.getString("MPA_NAME"));
        }

        return film;
    }

    private void updateFilmGenre(Film film) {
        String sqlQuery =
                "DELETE FROM film_genre WHERE film_id = ?";
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

    private Optional<Integer> getMpaId(Mpa mpa) {
        if ((mpa == null) || (mpa.getId() == null)) {
            return Optional.empty();
        } else {
            return Optional.of(mpa.getId());
        }
    }

    void setFilmGenres(List<Film> films) {
        if (films.size() < 1) {
            return;
        }

        // Собираем все id фильмов
        // Хотя здесь надо подходить аккуратно, т.к. некоторые версии sql-серверов начинают тупить при слишком
        // большом количестве значений в списке IN (...)
        StringBuilder enumId = new StringBuilder();
        for (Film film : films) {
            enumId.append(",").append(film.getId());
        }
        enumId.deleteCharAt(0);

        // Получаем жанры для указанных фильмов
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "SELECT film_genre.film_id, genre.id, genre.name \n" +
                        "FROM film_genre \n" +
                        "  INNER JOIN genre ON film_genre.genre_id = genre.id \n" +
                        "WHERE film_genre.film_id in (" + enumId + ")"
        );

        // Было бы здорово, если бы мы использовали для фильмов не List, а Map (сложность поиска элементов
        // O(n) против O(1) соответственно), тогда бы сложность нашего алгоритма была бы O(n).
        // Какого-то иного, более оптимального решения, чем использовать Map'у для сборки данных из RowSet'а
        // я не нашел, к сожалению.
        Map<Long, List<Genre>> filmGenres = new HashMap<>();

        while (rows.next()) {
            Long filmId = rows.getLong("film_id");
            if (!filmGenres.containsKey(filmId)) {
                filmGenres.put(filmId, new ArrayList<>());
            }

            Genre genre = new Genre();
            genre.setId(rows.getInt("id"));
            genre.setName(rows.getString("name"));
            filmGenres.get(filmId).add(genre);
        }

        // Обновляем жанры у списка фильмов
        for (Film film : films) {
            if (filmGenres.containsKey(film.getId())) {
                film.setGenres(filmGenres.get(film.getId()));
            }
        }
    }

    void setFilmGenres(Film film) {
        List<Film> films = new ArrayList<>();
        films.add(film);
        setFilmGenres(films);
    }

}
