package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@Component
public class FilmDatabaseStorage implements IFilmStorage {

    private final FilmDaoRepository filmDaoRepo;
    private final FilmRatingRepository filmRatingRepo;
    private final FilmGenreRepository filmGenreRepo;

    @Override
    public void add(Film film) {
        FilmDao dao = FilmDao.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .durationMinutes(film.getDurationMinutes())
                .build();
        FilmDao savedFilmDao = filmDaoRepo.save(dao);
        film.setId(savedFilmDao.getId());
        MpaRating rating = film.getMpaRating();
        if (rating != null)
            filmRatingRepo.save(new FilmRatingDao(savedFilmDao.getId(), rating.getId()));
    }

    @Override
    public void update(Film film) {
        // TODO
    }

    @Override
    public Film get(Long filmId) {
        // TODO
        return null;
    }

    @Override
    public List<Film> getAll() {
        // TODO
        return List.of();
    }

    @Override
    public void setLike(Long filmId, Long userId) {
        // TODO
    }

    @Override
    public void unsetLike(Long filmId, Long userId) {
        // TODO
    }

    @Override
    public Map<Long, Set<Long>> getLikedUserIdsByFilmId() {
        // TODO
        return Map.of();
    }
}
