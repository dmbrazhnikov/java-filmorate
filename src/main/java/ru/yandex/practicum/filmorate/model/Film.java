package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.IsAfter;
import ru.yandex.practicum.filmorate.validation.UpdateValidationGroup;
import java.time.LocalDate;


@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {

    @NotNull(groups = UpdateValidationGroup.class)
    private Integer id;

    @NotEmpty(message = "название не может быть пустым")
    private String name;

    @Size(max = 200, message = "описание должно содержать не более {max} символов")
    private String description;

    @IsAfter("1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
