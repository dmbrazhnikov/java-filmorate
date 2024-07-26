package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IFilmStorage {

    void add(Film film);

    void update(Film film);

    Film get(Integer filmId);

    List<Film> getAll();

    void setLike(Integer filmId, Integer userId);

    void unsetLike(Integer filmId, Integer userId);

    Map<Integer, Set<Integer>> getLikedUserIdsByFilmId();
}
