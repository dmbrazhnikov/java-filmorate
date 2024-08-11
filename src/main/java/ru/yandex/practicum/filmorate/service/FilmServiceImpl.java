package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.FilmDatabaseStorage;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import java.util.*;


@Service
public class FilmServiceImpl implements IFilmService {

    private final IFilmStorage filmStorage;
    private final IUserService userService;

    public FilmServiceImpl(FilmDatabaseStorage storage, UserServiceImpl userService) {
        filmStorage = storage;
        this.userService = userService;
    }

    // Сохранение
    @Override
    public Film add(Film film) {
        filmStorage.add(film);
        return film;
    }

    // Обновление
    @Override
    public Film update(Film film) {
        get(film.getId()); // Костыль: тест, в нарушение RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
        filmStorage.update(film);
        return film;
    }

    // Получение по ID
    @Override
    public Film get(Long id) {
        return Optional.ofNullable(filmStorage.get(id)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + id + " не найден")
        );
    }

    // Получение всех
    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    // Добавление отметки "Нравится"
    @Override
    public void setLike(Long filmId, Long userId) {
        Film film = get(filmId);
        User user = userService.get(userId);
        filmStorage.setLike(film.getId(), user.getId());
    }

    // Удаление отметки "Нравится"
    @Override
    public void unsetLike(Long filmId, Long userId) {
        Film film = get(filmId);
        User user = userService.get(userId);
        filmStorage.unsetLike(film.getId(), user.getId());
    }

    // Вывод 10 наиболее популярных фильмов
    @Override
    public List<Film> getMostPopular(int count) {
        SortedMap<Integer, Long> likesByFilmId = new TreeMap<>(Comparator.reverseOrder());
        filmStorage.getLikedUserIdsByFilmId().forEach((filmId, likedUserIds) -> {
            if (!likedUserIds.isEmpty())
                likesByFilmId.put(likedUserIds.size(), filmId);
        });
        return likesByFilmId.values().stream()
                .map(filmStorage::get)
                .limit(count)
                .toList();
    }
}
