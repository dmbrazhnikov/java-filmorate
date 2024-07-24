package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class NullValueException extends RuntimeException {
    private final String fieldWithNullValueName;
}
