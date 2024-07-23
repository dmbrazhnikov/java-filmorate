package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.stream.Collectors;


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
    public List<Film> getTopLikedFilms(int count) {
        SortedMap<Integer, Integer> likesByFilmId = new TreeMap<>(Comparator.reverseOrder());
        storage.getAll().forEach(film -> likesByFilmId.put(film.getId(), film.getLikes().size()));
        return likesByFilmId.values().stream()
                .map(storage::get)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film retrieveFilm(Integer id) {
        return Optional.ofNullable(storage.get(id)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + id + " не найден")
        );
    }
}
