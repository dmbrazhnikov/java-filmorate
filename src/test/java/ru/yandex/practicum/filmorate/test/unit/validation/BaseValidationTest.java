package ru.yandex.practicum.filmorate.test.unit.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BaseValidationTest {

    protected static Validator validator;
    protected static ValidatorFactory factory;

    @BeforeAll
    static void beforeAll() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void afterAll() {
        factory.close();
    }

    protected <T> void validateCorrect(T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    protected <T> void validateIncorrect(T entity, String errorMessage) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        assertNotNull(
                violations.stream()
                        .filter(v -> v.getMessage().equals(errorMessage))
                        .findFirst()
        );
    }
}
