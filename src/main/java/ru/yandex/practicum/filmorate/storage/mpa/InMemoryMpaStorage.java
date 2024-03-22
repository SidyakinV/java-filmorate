package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Slf4j
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
