package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class InMemoryFilmStorage implements Storage<Film, Integer> {

    private static final Map<Integer, Film> moviesById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    @Override
    public Film add(Film film) {
        int movieId = idSequence.getAndIncrement();
        film.setId(movieId);
        moviesById.put(movieId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        moviesById.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Integer filmId) {
        return moviesById.get(filmId);
    }

    @Override
    public List<Film> getAll() {
        return moviesById.values().stream().toList();
    }
}
