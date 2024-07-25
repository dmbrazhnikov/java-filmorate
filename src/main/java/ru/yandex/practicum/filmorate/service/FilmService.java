package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullValueException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class FilmService implements EntityService<Film, Integer> {

    private final InMemoryFilmStorage storage;
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    // Сохранение
    @Override
    public Film add(Film film) {
        int filmId = idSequence.getAndIncrement();
        film.setId(filmId);
        storage.add(film);
        return film;
    }

    // Обновление
    @Override
    public Film update(Film film) {
        if (film.getId() == null)
            throw new NullValueException("ru.yandex.practicum.filmorate.model.Film.id");
        get(film.getId()); // Костыль: тест, в нарушение RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
        storage.update(film);
        return film;
    }

    // Получение по ID
    @Override
    public Film get(Integer id) {
        return Optional.ofNullable(storage.get(id)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + id + " не найден")
        );
    }

    // Получение всех
    @Override
    public List<Film> getAll() {
        return storage.getAll();
    }

    // Добавление отметки "Нравится"
    public void setLike(Film film, User user) {
        Set<Integer> likedUsersIds = storage.getLikedUserIds(film.getId());
        likedUsersIds.add(user.getId());
        storage.setLikes(film.getId(), likedUsersIds);
    }

    // Удаление отметки "Нравится"
    public void unsetLike(Film film, User user) {
        Set<Integer> likedUsersIds = storage.getLikedUserIds(film.getId());
        if (!likedUsersIds.isEmpty()) {
            likedUsersIds.remove(user.getId());
            storage.setLikes(film.getId(), likedUsersIds);
        }
    }

    // Вывод 10 наиболее популярных фильмов
    public List<Film> getMostPopular(int count) {
        SortedMap<Integer, Integer> likesByFilmId = new TreeMap<>(Comparator.reverseOrder());
        storage.getLikedUserIdsByFilmId().forEach((filmId, likedUserIds) -> {
            if (!likedUserIds.isEmpty())
                likesByFilmId.put(likedUserIds.size(), filmId);
        });
        return likesByFilmId.values().stream()
                .map(storage::get)
                .limit(count)
                .toList();
    }
}
