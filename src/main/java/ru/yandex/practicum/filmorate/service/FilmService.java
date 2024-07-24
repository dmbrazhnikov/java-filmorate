package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;
import java.util.*;


@Service
public class FilmService {

    private final Storage<Film> storage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        storage = inMemoryFilmStorage;
    }

    // добавление отметки "Нравится"
    public void setLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    // удаление отметки "Нравится"
    public void unsetLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    // вывод 10 наиболее популярных фильмов
    public List<Film> getPopular(int count) {
        SortedMap<Integer, Integer> likesByFilmId = new TreeMap<>(Comparator.reverseOrder());
        storage.getAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .forEach(film -> likesByFilmId.put(film.getLikes().size(), film.getId()));
        return likesByFilmId.values().stream()
                .map(storage::get)
                .limit(count)
                .toList();
    }

    public Film retrieveFilm(Integer id) {
        return Optional.ofNullable(storage.get(id)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + id + " не найден")
        );
    }
}
