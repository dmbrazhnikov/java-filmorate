package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.mapper.GenreRowMapper;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public boolean existsById(Integer genreId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM genres WHERE id = " + genreId, Integer.class) > 0;
    }

    public List<Genre> findFilmGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(
                "SELECT g.* FROM film_genre fg, genres g WHERE fg.genre_id = g.id AND fg.film_id = ?" + filmId,
                genreRowMapper
        );
    }

    public void saveFilmGenres(Long filmId, List<Genre> genres) {

    }
}
