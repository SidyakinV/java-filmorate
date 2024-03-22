package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpaList() {
        List<Mpa> mpaList = new ArrayList<>();

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM rating");

        while (mpaRows.next()) {
            mpaList.add(mpaFromRowSet(mpaRows));
        }

        return mpaList;
    }

    @Override
    public Mpa getMpa(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM rating WHERE id = ?", id);

        if (!mpaRows.next()) {
            log.info("MPA-рейтинг с указанным ID {} не найден в базе данных", id);
            return null;
        }

        return mpaFromRowSet(mpaRows);
    }

    private Mpa mpaFromRowSet(SqlRowSet mpaRow) {
        Mpa mpa = new Mpa();
        mpa.setId(mpaRow.getInt("id"));
        mpa.setName(mpaRow.getString("name"));
        return mpa;
    }

}
