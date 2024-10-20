package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.FilmRowMapper;

import java.sql.Statement;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class FilmRepository implements IFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper rowMapper;

    @Override
    public boolean existsById(Long filmId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM films WHERE id = " + filmId, Integer.class) > 0;
    }

    @Override
    public void add(FilmDto filmDto) {
        final String query = String.format(
                "INSERT INTO films (name, description, release_date, duration_minutes) VALUES ('%s', '%s', '%s', %d)",
                filmDto.getName(),
                filmDto.getDescription(),
                filmDto.getReleaseDate().toString(),
                filmDto.getDurationMinutes()
        );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), keyHolder);
        Long newFilmId = keyHolder.getKey().longValue();
        filmDto.setId(newFilmId);
    }

    @Override
    public void update(FilmDto film) {
        if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM films WHERE id = " + film.getId(), Integer.class) == 0)
            // Костыль: тест, нарушая RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration_minutes = ? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().toString(),
                film.getDurationMinutes(),
                film.getId()
        );
        jdbcTemplate.update(
                "DELETE FROM film_rating WHERE film_id = ?",
                film.getId()
        );
        saveFilmRating(film);
        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ?",
                film.getId()
        );
        saveFilmGenres(film);
    }

    @Override
    public FilmDto get(Long filmId) {
        if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM films WHERE id = " + filmId, Integer.class) == 0)
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        //TODO
        return jdbcTemplate.queryForObject(
                "SELECT id, name, description, release_date, duration_minutes FROM films WHERE id = " + filmId,
                rowMapper,
                1
        );
    }

    @Override
    public List<FilmDto> getAll() {



        List<Film> filmDaos = filmDaoRepo.findAll();
        List<FilmDto> allFilms = new ArrayList<>();
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

    private void saveFilmRating(FilmDto film) {
        MpaRating rating = film.getMpa();
        if (rating != null) {
            if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM mpa_rating WHERE id = " + rating.getId(), Integer.class) == 0)
                throw new NotFoundException("Рейтинг с ID " + rating.getId() + " не найден");
            jdbcTemplate.update(
                    "INSERT INTO film_rating (film_id, rating_id) VALUES (?, ?)",
                    film.getId(),
                    rating.getId()
            );
        }
    }

    private void saveFilmGenres(FilmDto film) {
        List<Genre> filmGenres = film.getGenres();
        if (filmGenres != null && !filmGenres.isEmpty()) {
            filmGenres.forEach(genre -> {
                if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM genres WHERE id = " + genre.getId(), Integer.class) == 0)
                    throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                jdbcTemplate.update(
                        "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(),
                        genre.getId()
                );
            });
        }


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
}
