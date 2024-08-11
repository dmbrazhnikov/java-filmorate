package ru.yandex.practicum.filmorate.storage.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "film_genre")
public class FilmGenreDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO
    private Long filmId;
    private Long genreId;
}
