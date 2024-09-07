package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
