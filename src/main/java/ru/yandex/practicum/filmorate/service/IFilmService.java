package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;


public interface IFilmService {

    Film add(Film film);

    Film update(Film film);

    Film get(Long filmId);

    List<Film> getAll();

    void setLike(Long filmId, Long userId);

    void unsetLike(Long filmId, Long userId);

    List<Film> getMostPopular(int count);
}