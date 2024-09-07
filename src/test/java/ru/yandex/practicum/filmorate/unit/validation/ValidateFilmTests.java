package ru.yandex.practicum.filmorate.unit.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@DisplayName("Валидация атрибутов фильма")
public class ValidateFilmTests extends BaseValidationTest {

    private static Film refFilm;

    @Test
    @DisplayName("Корректные значения всех полей")
    void correctFilm() {
        validateCorrect(getRefFilm());
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Некорректное название")
    @MethodSource("provideWithIncorrectName")
    void incorrect(Film film, String errorMessage) {
        validateIncorrect(film, errorMessage);
    }

    @Nested
    @DisplayName("Описание")
    class DescriptionTests {

        @ParameterizedTest(name = "{0}")
        @DisplayName("Корректное")
        @MethodSource("provideWithCorrectDescription")
        void correct(Film film) {
            validateCorrect(film);
        }

        @Test
        @DisplayName("Превышение максимальной длины")
        void incorrect() {
            Film film = getRefFilm().toBuilder()
                    .name(new String(new char[200]).replace('\0', 'v'))
                    .build();
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertNotNull(
                    violations.stream()
                            .filter(v -> v.getMessage().equals("описание должно содержать не более 200 символов"))
                            .findFirst()
            );
        }

        private static Stream<Arguments> provideWithCorrectDescription() {
            refFilm = getRefFilm();
            return Stream.of(
                    arguments(named("null", refFilm.toBuilder().description(null).build())),
                    arguments(named("0 символов", refFilm.toBuilder().description("").build()))
            );
        }
    }

    @Nested
    @DisplayName("Дата релиза")
    class ReleaseDateTests {

        @Test
        @DisplayName("Самая ранняя из разрешённых")
        void sameDateAsAllowed() {
            Film film = getRefFilm().toBuilder()
                    .releaseDate(LocalDate.of(1895, 12, 29))
                    .build();
            validateCorrect(film);
        }

        @Test
        @DisplayName("Самая поздняя из неразрешённых")
        void dateBeforeAllowed() {
            Film film = getRefFilm().toBuilder()
                    .releaseDate(LocalDate.of(1895, 12, 28))
                    .build();
            validateIncorrect(film, "дата должна быть позже 1895-12-28");
        }
    }


    @Nested
    @DisplayName("Длительность")
    class DurationTests {

        @Test
        @DisplayName("Минимальная корректная")
        void correct() {
            Film film = getRefFilm().toBuilder()
                    .durationMinutes(1)
                    .build();
            validateCorrect(film);
        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("Некорректная")
        @MethodSource("provideWithIncorrectDuration")
        void incorrect(Film film, String errorMessage) {
            validateIncorrect(film, errorMessage);
        }

        private static Stream<Arguments> provideWithIncorrectDuration() {
            refFilm = getRefFilm();
            return Stream.of(
                    arguments(named("Ноль", refFilm.toBuilder().durationMinutes(0).build()), "должно быть больше 0"),
                    arguments(named("Отрицательная", refFilm.toBuilder().durationMinutes(-1).build()), "должно быть больше 0")
            );
        }
    }

    private static Stream<Arguments> provideWithIncorrectName() {
        refFilm = getRefFilm();
        return Stream.of(
                arguments(named("null", refFilm.toBuilder().name(null).build()),
                        "название не может быть пустым"),
                arguments(named("Пустая строка", refFilm.toBuilder().name("").build()),
                        "название не может быть пустым")
        );
    }

    private static Film getRefFilm() {
        return Film.builder()
                .name("Молчание ягнят")
                .durationMinutes(118)
                .releaseDate(LocalDate.of(1991, 2, 14))
                .build();
    }
}
