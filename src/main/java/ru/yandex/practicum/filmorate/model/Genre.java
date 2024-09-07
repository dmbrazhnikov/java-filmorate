package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @NotNull
    private Long id;
    private String name;
}
