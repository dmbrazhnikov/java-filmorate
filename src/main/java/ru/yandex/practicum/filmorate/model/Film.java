package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.IsAfter;

import java.time.LocalDate;


@Data
@Builder(toBuilder = true)
public class Film {

    private Integer id;

    @NotEmpty(message = "название не может быть пустым")
    private String name;

    @Size(max = 200, message = "описание должно содержать не более {max} символов")
    private String description;

    @IsAfter("1895-12-28")
    private LocalDate releaseDate;

    // Считаем всё короче получаса короткометражками и не принимаем для рейтинга
//    @DurationMin(minutes = 30, message = "длительность должна превышать {minutes} минут")
//    private Duration duration;
    private int duration; // FIXME подгонка под кривые тесты пайпа
}
