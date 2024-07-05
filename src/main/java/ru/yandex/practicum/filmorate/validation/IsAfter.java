package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy= IsAfterValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAfter {
    String value();
    String message() default "Дата должна быть позже {value}";
    // Class<?>[] groups() default {}; //TODO ???
    // Class<? extends Payload>[] payload() default {}; //TODO ???
}
