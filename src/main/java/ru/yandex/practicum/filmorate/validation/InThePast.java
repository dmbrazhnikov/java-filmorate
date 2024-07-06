package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = InThePastValidator.class)
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface InThePast {
    String message() default "Дата должна быть в прошлом";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
