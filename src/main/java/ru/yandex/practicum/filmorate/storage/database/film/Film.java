package ru.yandex.practicum.filmorate.storage.database;

import lombok.*;

import java.time.LocalDate;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer durationMinutes;
}
