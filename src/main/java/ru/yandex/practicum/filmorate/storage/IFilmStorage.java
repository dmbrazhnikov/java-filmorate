package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmDto;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IFilmStorage {

    void add(FilmDto film);

    void update(FilmDto film);

    FilmDto get(Long filmId);

    List<FilmDto> getAll();

    boolean existsById(Long filmId);

    void setLike(Long filmId, Long userId);

    void unsetLike(Long filmId, Long userId);

    Map<Long, Set<Long>> getAllLikesForFilmIds();
}
