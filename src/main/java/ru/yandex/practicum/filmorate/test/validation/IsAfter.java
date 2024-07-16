package ru.yandex.practicum.filmorate.test.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = IsAfterValidator.class)
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface IsAfter {
    String value();
    String message() default "дата должна быть позже {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
