package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmLikesRepository extends JpaRepository<FilmLikeDao, Long> {
    boolean existsByFilmIdAndUserId(Long filmId, Long userId);
    void deleteByFilmIdAndUserId(Long filmId, Long userId);
}