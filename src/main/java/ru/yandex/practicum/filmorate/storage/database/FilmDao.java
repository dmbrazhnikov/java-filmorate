package ru.yandex.practicum.filmorate.storage.database;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Entity
@Table(name = "films")
public class FilmDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name, description;
    private LocalDate releaseDate;
    private Integer durationMinutes;
}
