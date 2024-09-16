package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.FilmDto;
import java.util.List;


public interface IFilmService {

    FilmDto add(FilmDto film);

    FilmDto update(FilmDto film);

    FilmDto get(Long filmId);

    List<FilmDto> getAll();

    void setLike(Long filmId, Long userId);

    void unsetLike(Long filmId, Long userId);

    List<FilmDto> getMostPopular(int count);
}