package ru.yandex.practicum.filmorate.storage.database.film;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRatingRepository extends JpaRepository<FilmRatingDao, FilmRatingDao> {

    void deleteAllByFilmId(Long filmId);

    FilmRatingDao findFirstByFilmId(Long filmId);
}
