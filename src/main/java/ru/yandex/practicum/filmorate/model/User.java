package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;

    @NotNull(message = "логин не может быть пустым")
    @Pattern(regexp = "^[^ ]+$", message = "некорректный логин")
    private String login;

    private String name;

    @NotNull(message = "адрес электронной почты не может быть пустым")
    @Email(message = "некорректный адрес электронной почты", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Past(message = "дата рождения должна быть в прошлом")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public String getName() {
        return name == null || name.isEmpty() || name.isBlank() ? login : name;
    }
}
