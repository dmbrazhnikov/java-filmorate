package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.InThePast;

import java.time.LocalDate;


@Data
public class User {

    private Integer id;

    @Email(message = "Некорректный адрес электронной почты", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Pattern(regexp = "^[^ ]+$", message = "Некорректный логин")
    private String login;

    private String name;

    @InThePast
    private LocalDate birthDate;

    public String getName() {
        return name.isEmpty() || name.isBlank() ? login : name;
    }
}
