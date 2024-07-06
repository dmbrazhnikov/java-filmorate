package ru.yandex.practicum.filmorate.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private final String message;
    private final String description;
    private List<FieldErrorDTO> fieldErrors;

    public ErrorDTO(String message) {
        this(message, null);
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null)
            fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldErrorDTO(objectName, field, message));
    }
}
