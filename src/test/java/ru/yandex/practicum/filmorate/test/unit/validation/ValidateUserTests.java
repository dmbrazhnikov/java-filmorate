package ru.yandex.practicum.filmorate.test.unit.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.test.model.User;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@DisplayName("Валидация атрибутов пользователя")
public class ValidateUserTests extends BaseValidationTest {

    private static User refUser;

    @Test
    @DisplayName("Корректные значения всех полей")
    void correctUser() {
        validateCorrect(getRefUser());
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Некорректный логин")
    @MethodSource("provideWithIncorrectLogin")
    void incorrectLogin(User user, String errorMessage) {
        validateIncorrect(user, errorMessage);
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Некорректный адрес электронной почты")
    @MethodSource("provideWithIncorrectEmail")
    void incorrectEmail(User user, String errorMessage) {
        validateIncorrect(user, errorMessage);
    }

    @Test
    @DisplayName("Самая поздняя корректная дата рождения")
    void latestCorrectBirthDate() {
        User user = getRefUser().toBuilder()
                .birthday(LocalDate.now().minusDays(1))
                .build();
        validateCorrect(user);
    }

    @Test
    @DisplayName("Самая ранняя некорректная дата рождения")
    void earliestIncorrectBirthDate() {
        User user = getRefUser().toBuilder()
                .birthday(LocalDate.now())
                .build();
        validateIncorrect(user, "дата рождения должна быть в прошлом");
    }


    private static Stream<Arguments> provideWithIncorrectLogin() {
        refUser = getRefUser();
        return Stream.of(
                arguments(named("null", refUser.toBuilder().login(null).build()),
                        "логин не может быть пустым"),
                arguments(named("0 символов", refUser.toBuilder().login("").build()),
                        "некорректный логин"),
                arguments(named("С пробелом", refUser.toBuilder().login("User 123").build()),
                        "некорректный логин")
        );
    }

    private static Stream<Arguments> provideWithIncorrectEmail() {
        refUser = getRefUser();
        return Stream.of(
                arguments(named("null", refUser.toBuilder().email(null).build()),
                        "адрес электронной почты не может быть пустым"),
                arguments(named("пустой", refUser.toBuilder().email("").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("набор разрешённых символов", refUser.toBuilder().email("user123+*-").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("логин.хост.зона", refUser.toBuilder().email("user123.server.com").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("почтовый сервер без зоны", refUser.toBuilder().email("user123@server").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("спецсимволы", refUser.toBuilder().email("user\\);123@server.com").build()),
                        "некорректный адрес электронной почты")
        );
    }

    private static User getRefUser() {
        return User.builder()
                .login("User1.-$")
                .name("Тестовый пользователь 1")
                .email("user1@server.com")
                .birthday(LocalDate.of(1990, 5, 7))
                .build();
    }
}
