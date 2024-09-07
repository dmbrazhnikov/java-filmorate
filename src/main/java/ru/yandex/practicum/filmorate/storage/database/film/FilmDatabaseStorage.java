package ru.yandex.practicum.filmorate.storage.database.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreRepository;
import ru.yandex.practicum.filmorate.storage.database.MpaRatingRepository;
import java.util.*;


@RequiredArgsConstructor
@Component
@Transactional
public class FilmDatabaseStorage implements IFilmStorage {

    private final FilmDaoRepository filmDaoRepo;
    private final FilmRatingRepository filmRatingRepo;
    private final FilmGenreRepository filmGenreRepo;
    private final MpaRatingRepository ratingRepo;
    private final GenreRepository genreRepo;
    private final FilmLikesRepository filmLikesRepo;

    @Override
    public void add(Film film) {
        FilmDao filmDao = FilmDao.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .durationMinutes(film.getDurationMinutes())
                .build();
        FilmDao savedFilmDao = filmDaoRepo.save(filmDao);
        film.setId(savedFilmDao.getId());
        saveFilmRating(film);
        saveFilmGenres(film);
    }

    @Override
    public void update(Film film) {
        if (!filmDaoRepo.existsById(film.getId()))
            // Костыль: тест, нарушая RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        FilmDao dao = FilmDao.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .durationMinutes(film.getDurationMinutes())
                .build();
        filmDaoRepo.save(dao);
        filmRatingRepo.deleteAllByFilmId(film.getId());
        saveFilmRating(film);
        filmGenreRepo.deleteAllByFilmId(film.getId());
        saveFilmGenres(film);
    }

    @Override
    public Film get(Long filmId) {
        FilmDao dao = filmDaoRepo.findById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + filmId + " не найден")
        );
        return getFilmByDao(dao);
    }

    @Override
    public List<Film> getAll() {
        List<FilmDao> filmDaos = filmDaoRepo.findAll();
        List<Film> allFilms = new ArrayList<>();
        filmDaos.forEach(filmDao -> allFilms.add(getFilmByDao(filmDao)));
        return allFilms;
    }

    @Override
    public void setLike(Long filmId, Long userId) {
        /* Существование фильма и пользователя с переданными идентификаторами проверяются сервисом, так что тут
        априори считаем, что идентификаторы действительные */
        if (!filmLikesRepo.existsByFilmIdAndUserId(filmId, userId))
            filmLikesRepo.save(
                    FilmLikeDao.builder()
                            .filmId(filmId)
                            .userId(userId)
                            .build()
            );
    }

    @Override
    public void unsetLike(Long filmId, Long userId) {
        if (filmLikesRepo.existsByFilmIdAndUserId(filmId, userId))
            filmLikesRepo.deleteByFilmIdAndUserId(filmId, userId);
    }

    @Override
    public Map<Long, Set<Long>> getAllLikesForFilmIds() {
        Map<Long, Set<Long>> result = new HashMap<>();
        List<FilmLikeDao> filmLikes = filmLikesRepo.findAll();
        filmLikes.forEach(filmLikeDao -> {
            Set<Long> likedUserIds = result.computeIfAbsent(filmLikeDao.getFilmId(), v -> new HashSet<>());
            likedUserIds.add(filmLikeDao.getUserId());
            result.put(filmLikeDao.getFilmId(), likedUserIds);
        });
        return result;
    }

    private void saveFilmRating(Film film) {
        MpaRating rating = film.getMpa();
        if (rating != null) {
            if (!ratingRepo.existsById(rating.getId()))
                throw new NotFoundException("Рейтинг с ID " + rating.getId() + " не найден");
            filmRatingRepo.save(new FilmRatingDao(film.getId(), rating.getId()));
        }
    }

    private void saveFilmGenres(Film film) {
        Optional.ofNullable(film.getGenres()).ifPresent(
                genres -> {
                    List<FilmGenreDao> filmGenreDaos = new ArrayList<>(genres.size());
                    genres.forEach(genre -> {
                        if (!genreRepo.existsById(genre.getId()))
                            throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                        filmGenreDaos.add(
                                FilmGenreDao.builder()
                                        .filmId(film.getId())
                                        .genreId(genre.getId())
                                        .build()
                        );
                    });
                    filmGenreRepo.saveAll(filmGenreDaos);
                }
        );
    }

    private Film getFilmByDao(FilmDao filmDao) {
        Film film = Film.builder()
                .id(filmDao.getId())
                .name(filmDao.getName())
                .description(filmDao.getDescription())
                .durationMinutes(filmDao.getDurationMinutes())
                .releaseDate(filmDao.getReleaseDate())
                .build();
        FilmRatingDao filmRating = filmRatingRepo.findFirstByFilmId(filmDao.getId());
        if (filmRating != null)
            film.setMpa(ratingRepo.findById(filmRating.getRatingId()).get());
        List<FilmGenreDao> filmGenreDaos = filmGenreRepo.findAllByFilmId(filmDao.getId());
        List<Genre> filmGenres = filmGenreDaos.stream()
                .map(filmGenreDao -> genreRepo.findById(filmGenreDao.getGenreId()).get())
                .toList();
        film.setGenres(filmGenres);
        return film;
    }
}
