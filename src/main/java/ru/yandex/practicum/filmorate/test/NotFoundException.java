package ru.yandex.practicum.filmorate.test;

import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.HttpStatus.*;


@ResponseStatus(NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
