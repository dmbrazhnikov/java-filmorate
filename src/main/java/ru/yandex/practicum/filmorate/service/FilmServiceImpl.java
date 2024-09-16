package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.TestsWorkaroundException;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.Film;
import ru.yandex.practicum.filmorate.storage.database.FilmRepository;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreRepository;
import ru.yandex.practicum.filmorate.storage.database.MpaRatingRepository;

import java.util.*;


@Service
public class FilmServiceImpl implements IFilmService {

    private final IFilmStorage filmRepo;
    private final GenreRepository genreRepo;
    private final MpaRatingRepository ratingRepo;
    private final IUserService userService;

    public FilmServiceImpl(
            FilmRepository filmRepo,
            UserServiceImpl userService,
            GenreRepository genreRepo,
            MpaRatingRepository ratingRepo
    ) {
        this.filmRepo = filmRepo;
        this.userService = userService;
        this.genreRepo = genreRepo;
        this.ratingRepo = ratingRepo;
    }

    // Сохранение
    @Override
    public FilmDto add(FilmDto filmDto) {
        checkFilmGenresAndRating(filmDto);
        filmRepo.add(filmDto);
        ratingRepo.saveFilmRating(filmDto.getId(), filmDto.getMpa().getId());


        // TODO
        saveFilmGenres(filmDto);

        return filmDto;
    }

    // Обновление
    @Override
    public FilmDto update(FilmDto film) {
        checkFilmGenresAndRating(film);
        filmStorage.update(film);
        return film;
    }

    // Получение по ID
    @Override
    public FilmDto get(Long id) {
        return filmStorage.get(id);
    }

    // Получение всех
    @Override
    public List<FilmDto> getAll() {
        return filmStorage.getAll();
    }

    // Добавление отметки "Нравится"
    @Override
    public void setLike(Long filmId, Long userId) {
        FilmDto film = get(filmId);
        User user = userService.get(userId);
        filmStorage.setLike(film.getId(), user.getId());
    }

    // Удаление отметки "Нравится"
    @Override
    public void unsetLike(Long filmId, Long userId) {
        FilmDto film = get(filmId);
        User user = userService.get(userId);
        filmStorage.unsetLike(film.getId(), user.getId());
    }

    // Вывод 10 наиболее популярных фильмов
    @Override
    public List<FilmDto> getMostPopular(int count) {
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

    private void checkFilmGenresAndRating(FilmDto film) {
        if (film.getMpa() != null && !ratingRepo.existsById(film.getMpa().getId()))
            throw new TestsWorkaroundException("Рейтинг с ID " + film.getMpa().getId() + " не найден");
        Optional<List<Genre>> genresOpt = Optional.ofNullable(film.getGenres());
        if (genresOpt.isPresent()) {
            Set<Genre> genresTreeSet = new TreeSet<>(Comparator.comparing(Genre::getId));
            genresOpt.get().forEach(
                    genre -> {
                        if (!genreRepo.existsById(genre.getId()))
                            throw new TestsWorkaroundException("Жанр с ID " + genre.getId() + " не найден");
                        genresTreeSet.add(genre);
                    }
            );
            film.setGenres(genresTreeSet.stream().toList());
        }
    }
}
