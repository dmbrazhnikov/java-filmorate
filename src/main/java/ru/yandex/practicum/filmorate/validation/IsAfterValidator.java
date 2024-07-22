package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;


public class IsAfterValidator implements ConstraintValidator<IsAfter, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(IsAfter annotation) {
        date = LocalDate.parse(annotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(date);
    }
}