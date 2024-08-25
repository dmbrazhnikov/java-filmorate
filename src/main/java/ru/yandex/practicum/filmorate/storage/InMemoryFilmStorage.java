package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class InMemoryFilmStorage implements IFilmStorage {

    private final Map<Long, Film> filmsById = new HashMap<>();
    private final Map<Long, Set<Long>> likedUserIdsByFilmId = new HashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);


    @Override
    public void add(Film film) {
        film.setId(idSeq.getAndIncrement());
        filmsById.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        Long filmId = OptionalLong.of(film.getId()).orElse(idSeq.getAndIncrement());
        film.setId(filmId);
        filmsById.put(film.getId(), film);
    }

    @Override
    public Film get(Long filmId) {
        return filmsById.get(filmId);
    }

    @Override
    public List<Film> getAll() {
        return filmsById.values().stream().toList();
    }

    @Override
    public void setLike(Long filmId, Long userId) {
        Set<Long> likedUserIds = likedUserIdsByFilmId.computeIfAbsent(filmId, v -> new HashSet<>());
        likedUserIds.add(userId);
        likedUserIdsByFilmId.put(filmId, likedUserIds);
    }

    @Override
    public void unsetLike(Long filmId, Long userId) {
        Set<Long> likedUserIds = likedUserIdsByFilmId.get(filmId);
        if (likedUserIds != null)
            likedUserIds.remove(userId);
    }

    @Override
    public Map<Long, Set<Long>> getAllLikesForFilmIds() {
        return new HashMap<>(likedUserIdsByFilmId);
    }
}
