package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.TestsWorkaroundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.film.FilmDatabaseStorage;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreRepository;
import ru.yandex.practicum.filmorate.storage.database.MpaRatingRepository;
import java.util.*;


@Service
public class FilmServiceImpl implements IFilmService {

    private final IFilmStorage filmStorage;
    private final IUserService userService;
    private final MpaRatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    public FilmServiceImpl(
            FilmDatabaseStorage storage,
            UserServiceImpl userService,
            MpaRatingRepository ratingRepository,
            GenreRepository genreRepository
    ) {
        filmStorage = storage;
        this.userService = userService;
        this.ratingRepository = ratingRepository;
        this.genreRepository = genreRepository;
    }

    // Сохранение
    @Override
    public Film add(Film film) {
        checkFilmGenresAndRating(film);
        filmStorage.add(film);
        return film;
    }

    // Обновление
    @Override
    public Film update(Film film) {
        checkFilmGenresAndRating(film);
        filmStorage.update(film);
        return film;
    }

    // Получение по ID
    @Override
    public Film get(Long id) {
        return filmStorage.get(id);
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
        filmStorage.getAllLikesForFilmIds().forEach((filmId, likedUserIds) -> {
            if (!likedUserIds.isEmpty())
                likesByFilmId.put(likedUserIds.size(), filmId);
        });
        return likesByFilmId.values().stream()
                .map(filmStorage::get)
                .limit(count)
                .toList();
    }

    private void checkFilmGenresAndRating(Film film) {
        if (!ratingRepository.existsById(film.getMpa().getId()))
            throw new TestsWorkaroundException("Рейтинг с ID " + film.getMpa().getId() + " не найден");
        Optional<List<Genre>> genresOpt = Optional.ofNullable(film.getGenres());
        if (genresOpt.isPresent()) {
            Set<Genre> genresTreeSet = new TreeSet<>(Comparator.comparing(Genre::getId));
            genresOpt.get().forEach(
                    genre -> {
                        if (!genreRepository.existsById(genre.getId()))
                            throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                        genresTreeSet.add(genre);
                    }
            );
            film.setGenres(genresTreeSet.stream().toList());
        }
    }
}
