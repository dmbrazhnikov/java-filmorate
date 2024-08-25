package ru.yandex.practicum.filmorate.storage.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "film_rating")
@IdClass(FilmRatingDao.class)
public class FilmRatingDao {
    @Id
    private Long filmId, ratingId;
}
