package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IFilmStorage {

    void add(Film film);

    void update(Film film);

    Film get(Long filmId);

    List<Film> getAll();

    void setLike(Long filmId, Long userId);

    void unsetLike(Long filmId, Long userId);

    Map<Long, Set<Long>> getLikedUserIdsByFilmId();
}
