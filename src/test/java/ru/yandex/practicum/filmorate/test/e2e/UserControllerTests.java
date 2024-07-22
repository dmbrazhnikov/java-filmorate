package ru.yandex.practicum.filmorate.test.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.test.FilmorateApplication;
import ru.yandex.practicum.filmorate.test.model.Film;
import ru.yandex.practicum.filmorate.test.model.User;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


@DisplayName("Пользователь")
@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = RANDOM_PORT)
public class UserControllerTests {

    private static final RestAssuredClient userClient = new RestAssuredClient("/users");
    private static User refUser;

    @LocalServerPort
    int port;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
        refUser = getRefUser();
    }

    /* Некоторые проверки преднамеренно упрощены (неполны) для ускорения разработки */

    @Test
    @DisplayName("Добавление с корректными атрибутами")
    void addValid() {
        userClient.sendPostRequest(refUser)
                .then()
                .statusCode(CREATED.value())
                .and()
                .assertThat().body("id", notNullValue(Integer.class));
    }

    @DisplayName("Добавление с одним некорректным атрибутом")
    @ParameterizedTest(name = "{0}")
    @MethodSource({"provideUsersWithSingleNonValidAttribute"})
    void badRequest(User user, String errorMessage) {
        userClient.sendPostRequest(user)
                .then()
                .statusCode(BAD_REQUEST.value())
                .and()
                .assertThat().body("fieldErrors[0].message", equalToIgnoringCase(errorMessage));
    }

    @DisplayName("Получение всех")
    @Test
    void getAllUsers() {
        User anotherUser = User.builder()
                .login("user2")
                .name("Тестовый пользователь 2")
                .email("user2@server.com")
                .birthday(LocalDate.of(1982, 11, 19))
                .build();
        userClient.sendPostRequest(anotherUser);
        userClient.sendGet("")
                .then()
                .statusCode(OK.value())
                .and()
                .assertThat().body("size()", greaterThan(1));
    }

    @DisplayName("Обновление")
    @Test
    void update() {
        int userId = userClient.sendPostRequest(refUser).path("id");
        String newName = "Василий";
        User updatedUser = refUser.toBuilder()
                .id(userId)
                .name(newName)
                .build();
        userClient.sendPutRequest(updatedUser)
                .then()
                .statusCode(OK.value())
                .and()
                .assertThat().body("name", equalTo(newName));
    }

    @DisplayName("Получение по ID существующего")
    @Test
    void getExistingById() {
        int userId = userClient.sendPostRequest(refUser).path("id");
        refUser.setId(userId);
        User result = userClient.sendGet("/" + userId)
                .then()
                .statusCode(OK.value())
                .extract()
                .as(User.class);
        assertThat(result).isEqualTo(refUser);
    }

    @DisplayName("Получение по ID несуществующего")
    @Test
    void getNonExistingById() {
        userClient.sendGet("/" + 9999)
                .then()
                .statusCode(NOT_FOUND.value());
    }

    private static Stream<Arguments> provideUsersWithSingleNonValidAttribute() {
        refUser = getRefUser();
        return Stream.of(
                arguments(named("Логин null", refUser.toBuilder().login(null).build()),
                        "логин не может быть пустым"),
                arguments(named("Пустой логин", refUser.toBuilder().login("").build()),
                        "некорректный логин"),
                arguments(named("Пробел в логине", refUser.toBuilder().login("User 123").build()),
                        "некорректный логин"),
                arguments(named("Адрес эл. почты null", refUser.toBuilder().email(null).build()),
                        "адрес электронной почты не может быть пустым"),
                arguments(named("Пустой адрес эл. почты", refUser.toBuilder().email("").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("Невалидный адрес эл. почты",
                                refUser.toBuilder().email("user123*.server.com").build()),
                        "некорректный адрес электронной почты"),
                arguments(named("Дата рождения - сегодня", refUser.toBuilder().birthday(LocalDate.now()).build()),
                        "дата рождения должна быть в прошлом")
        );
    }

    private static User getRefUser() {
        return User.builder()
                .login("user1")
                .name("Тестовый пользователь 1")
                .email("user1@server.com")
                .birthday(LocalDate.of(1990, 5, 7))
                .build();
    }
}
