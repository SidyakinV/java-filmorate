package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getMpaList() {
        log.debug("Запрос на получение списка MPA-рейтингов");
        return mpaStorage.getMpaList();
    }

    public Mpa getMpa(Integer id) throws NotFoundException {
        log.debug("Запрос на получение информации о MPA-рейтинге по идентификатору (ID: {})", id);
        Mpa mpa = mpaStorage.getMpa(id);
        if (mpa == null) {
            throw new NotFoundException(String.format("MPA-рейтинг с указанным ID %d не найден в базе данных", id));
        }
        return mpa;
    }
}
