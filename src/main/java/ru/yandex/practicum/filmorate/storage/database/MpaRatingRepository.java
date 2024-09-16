package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.database.mapper.MpaRatingRowMapper;

@Repository
@RequiredArgsConstructor
public class MpaRatingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingRowMapper rowMapper;

    public boolean existsById(Integer ratingId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM mpa_rating WHERE id = " + ratingId, Integer.class) > 0;
    }

    public MpaRating findFilmRatingByFilmId(Long filmId) {
        return jdbcTemplate.queryForObject(
                "SELECT g.* FROM film_rating fr, mpa_rating r WHERE fr.rating_id = r.id AND fr.film_id = ?" + filmId,
                rowMapper
        );
    }

    public void saveFilmRating(Long filmId, MpaRating rating) {
        jdbcTemplate.update(
                "INSERT INTO film_rating (film_id, rating_id) VALUES (?, ?)",
                filmId,
                rating.getId()
        );
    }
}
