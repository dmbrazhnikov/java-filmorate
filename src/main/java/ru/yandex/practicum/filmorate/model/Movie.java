package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.validation.IsAfter;
import ru.yandex.practicum.filmorate.validation.InThePast;
import java.time.Duration;
import java.time.LocalDate;


@Data
public class Movie {

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "{validation.name.size.too_long}")
    @IsAfter("1895-12-28")
    private String description;

    @InThePast
    private LocalDate releaseDate;

    @NotBlank
    @DurationMin(minutes = 30)
    private Duration duration;
}
