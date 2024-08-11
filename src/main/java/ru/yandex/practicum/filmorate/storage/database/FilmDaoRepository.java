package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FilmDaoRepository extends JpaRepository<FilmDao, Long> {
}
