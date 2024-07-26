package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;


public interface IFilmService {

    Film add(Film film);

    Film update(Film film);

    Film get(Integer filmId);

    List<Film> getAll();

    void setLike(Integer filmId, Integer userId);

    void unsetLike(Integer filmId, Integer userId);

    List<Film> getMostPopular(int count);
}