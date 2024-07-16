package ru.yandex.practicum.filmorate.test.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
