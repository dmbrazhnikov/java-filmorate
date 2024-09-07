package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "mpa_rating")
public class MpaRating {

    @Id
    @NotNull
    private Long id;

    private String name;
}
