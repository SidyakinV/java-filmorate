package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    public List<Mpa> getMpaList();

    public Mpa getMpa(Integer id);

}
