package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.InThePast;
import java.time.LocalDate;


@Data
@Builder(toBuilder = true)
public class User {

    private Integer id;

    @NotNull(message = "логин не может быть пустым")
    @Pattern(regexp = "^[^ ]+$", message = "некорректный логин")
    private String login;

    /*
    * В требованиях сказано: "имя для отображения может быть пустым — в таком случае будет использован логин"
    * Отображение - это область фронтенда. Этот проект - бэкенд, поэтому в классе есть специальный геттер,
    * но нет валидации или ужимок типа назначения имени, равного логину, при создании объекта.
    * */
    private String name;

    @NotNull(message = "адрес электронной почты не может быть пустым")
    @Email(message = "некорректный адрес электронной почты", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @InThePast(message = "дата рождения должна быть в прошлом")
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isEmpty() || name.isBlank() ? login : name;
    }
}
