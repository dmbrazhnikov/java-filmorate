package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;


public class InThePastValidator implements ConstraintValidator<InThePast, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value != null)
            if (!value.isBefore(LocalDate.now()))
                valid = false;
        return valid;
    }
}
