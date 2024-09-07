package ru.yandex.practicum.filmorate.storage.database.film;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface FilmGenreRepository extends JpaRepository<FilmGenreDao, Long> {

    void deleteAllByFilmId(Long filmId);

    List<FilmGenreDao> findAllByFilmId(Long filmId);
}
