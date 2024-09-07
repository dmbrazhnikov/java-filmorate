package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Repository
public interface MpaRatingRepository extends JpaRepository<MpaRating, Long> {
}
