package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component("memMpaStorage")
public class InMemoryMpaStorage implements MpaStorage {
    @Override
    public List<Mpa> getMpaList() {
        return null;
    }

    @Override
    public Mpa getMpa(Integer id) {
        return null;
    }
}
