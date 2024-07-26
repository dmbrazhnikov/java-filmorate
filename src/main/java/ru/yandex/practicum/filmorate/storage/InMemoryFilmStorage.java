package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;


@Component
public class InMemoryFilmStorage implements IFilmStorage {

    private final Map<Integer, Film> filmsById = new HashMap<>();
    private final Map<Integer, Set<Integer>> likedUserIdsByFilmId = new HashMap<>();


    @Override
    public void add(Film film) {
        filmsById.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        filmsById.put(film.getId(), film);
    }

    @Override
    public Film get(Integer filmId) {
        return filmsById.get(filmId);
    }

    @Override
    public List<Film> getAll() {
        return filmsById.values().stream().toList();
    }

    @Override
    public void setLike(Integer filmId, Integer userId) {
        Set<Integer> likedUserIds = Optional.ofNullable(likedUserIdsByFilmId.get(filmId)).orElse(new HashSet<>());
        likedUserIds.add(userId);
        likedUserIdsByFilmId.put(filmId, likedUserIds);
    }

    @Override
    public void unsetLike(Integer filmId, Integer userId) {
        Set<Integer> likedUserIds = likedUserIdsByFilmId.get(filmId);
        if (likedUserIds != null)
            likedUserIds.remove(userId);
    }

    @Override
    public Map<Integer, Set<Integer>> getLikedUserIdsByFilmId() {
        return new HashMap<>(likedUserIdsByFilmId);
    }
}
