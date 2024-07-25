package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;


@Component
public class InMemoryFilmStorage implements Storage<Film, Integer> {

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

    public Set<Integer> getLikedUserIds(Integer filmId) {
        return Optional.ofNullable(likedUserIdsByFilmId.get(filmId)).orElse(new HashSet<>());
    }

    public void setLikes(Integer filmId, Set<Integer> likedUserIds) {
        likedUserIdsByFilmId.put(filmId, likedUserIds);
    }

    public Map<Integer, Set<Integer>> getLikedUserIdsByFilmId() {
        return new HashMap<>(likedUserIdsByFilmId);
    }
}
