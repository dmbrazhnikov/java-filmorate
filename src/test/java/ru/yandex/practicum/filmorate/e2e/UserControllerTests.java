package ru.yandex.practicum.filmorate.e2e;

import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


@DisplayName("Пользователь")
@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = RANDOM_PORT)
public class UserControllerTests extends BaseTest {

    private static final RestAssuredClient userClient = new RestAssuredClient("/users");
    private static User refUser;

    @LocalServerPort
    int port;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
        refUser = getTestUser();
    }

    /* Некоторые проверки преднамеренно упрощены (неполны) для ускорения разработки */

    @Test
    @DisplayName("Добавление с корректными атрибутами")
    void addValid() {
        userClient.sendPost(refUser)
                .then()
                .statusCode(CREATED.value())
                .and()
                .assertThat().body("id", notNullValue(Integer.class));
    }

    @DisplayName("Добавление с одним некорректным атрибутом")
    @ParameterizedTest(name = "{0}")
    @MethodSource({"provideUsersWithSingleNonValidAttribute"})
    void badRequest(User user, String errorMessage) {
        userClient.sendPost(user)
                .then()
                .statusCode(BAD_REQUEST.value())
                .and()
                .assertThat().body("fieldErrors[0].message", equalToIgnoringCase(errorMessage));
    }

    @DisplayName("Получение всех")
    @Test
    void getAllUsers() {
        User anotherUser = getTestUser();
        userClient.sendPost(anotherUser);
        userClient.sendGet("")
                .then()
                .statusCode(OK.value())
                .and()
                .assertThat().body("size()", greaterThan(1));
    }

    @DisplayName("Обновление")
    @Test
    void update() {
        int userId = userClient.sendPost(refUser).path("id");
        String newName = "Василий";
        User updatedUser = refUser.toBuilder()
                .id(userId)
                .name(newName)
                .build();
        userClient.sendPutWithPayload(updatedUser)
                .then()
                .statusCode(OK.value())
                .and()
                .assertThat().body("name", equalTo(newName));
    }

    @DisplayName("Получение по ID существующего")
    @Test
    void getExistingById() {
        int userId = userClient.sendPost(refUser).path("id");
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

    @Nested
    @DisplayName("Друзья")
    class FriendshipTests {

        private static User user1, user2;

        @BeforeEach
        void beforeEach() {
            user1 = userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
            user2 = userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
        }

//        @Test
//        @DisplayName("Добавление с обоими существующими")
//        void setFriendshipForExisting() {
//            userClient.sendPutWithoutPayload(String.format("/%d/friends/%d", user1.getId(), user2.getId()))
//                    .then()
//                    .statusCode(NO_CONTENT.value());
//            User actualUser1 = userClient.sendGet("/" + user1.getId())
//                    .then()
//                    .extract()
//                    .as(User.class);
//            User actualUser2 = userClient.sendGet("/" + user2.getId())
//                    .then()
//                    .extract()
//                    .as(User.class);
//            assertAll(
//                    () -> assertTrue(actualUser1.getFriends().contains(user2.getId())),
//                    () -> assertTrue(actualUser2.getFriends().contains(user1.getId()))
//            );
//        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("Добавление с одним несуществующим")
        @MethodSource("provideForFriendshipWithNonExisting")
        void setFriendshipWithNonExisting(String pathSuffix) {
            userClient.sendPutWithoutPayload(pathSuffix)
                    .then()
                    .statusCode(NOT_FOUND.value());
        }

//        @Test
//        @DisplayName("Удаление с обоими существующими")
//        void unsetFriendshipForExisting() {
//            userClient.sendDelete(String.format("/%d/friends/%d", user1.getId(), user2.getId()))
//                    .then()
//                    .statusCode(NO_CONTENT.value());
//            user1 = userClient.sendGet("/" + user1.getId())
//                    .then()
//                    .extract()
//                    .as(User.class);
//            assertFalse(user1.getFriends().contains(user2.getId()));
//        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("Удаление с одним несуществующим")
        @MethodSource("provideForFriendshipWithNonExisting")
        void unsetFriendshipForNonExisting(String pathSuffix) {
            userClient.sendDelete(pathSuffix)
                    .then()
                    .statusCode(NOT_FOUND.value());
        }

        @Test
        @DisplayName("Список общих друзей")
        void mutualFriendIds() {
            User user3 = userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
            User user4 = userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
            User user5 = userClient.sendPost(getTestUser())
                            .then()
                            .extract()
                            .as(User.class);
            userClient.sendPutWithoutPayload(String.format("/%d/friends/%d", user1.getId(), user3.getId()));
            userClient.sendPutWithoutPayload(String.format("/%d/friends/%d", user2.getId(), user4.getId()));
            userClient.sendPutWithoutPayload(String.format("/%d/friends/%d", user1.getId(), user5.getId()));
            userClient.sendPutWithoutPayload(String.format("/%d/friends/%d", user2.getId(), user5.getId()));
            user5 = userClient.sendGet("/" + user5.getId())
                    .then()
                    .extract()
                    .as(User.class);
            List<User> mutualFriends1 = userClient.sendGet(String.format("/%d/friends/common/%d", user1.getId(), user2.getId()))
                    .then()
                    .statusCode(OK.value())
                    .extract()
                    .as(new UserListTypeToken().getType());
            List<User> mutualFriends2 = userClient.sendGet(String.format("/%d/friends/common/%d", user2.getId(), user1.getId()))
                    .then()
                    .statusCode(OK.value())
                    .extract()
                    .as(new UserListTypeToken().getType());
            User mutualFriend = user5;
            assertAll(
                    () -> assertEquals(mutualFriends1.get(0), mutualFriend),
                    () -> assertEquals(mutualFriends1, mutualFriends2)
            );
        }

        class UserListTypeToken extends TypeToken<List<User>> {
        }

        private static Stream<Arguments> provideForFriendshipWithNonExisting() {
            user1 = userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
            return Stream.of(
                    arguments(named("Существующий пользователь, несуществующий друг", String.format("/%d/friends/%d", user1.getId(), 9999))),
                    arguments(named("Несуществующий пользователь, существующий друг", String.format("/%d/friends/%d", 9999, user1.getId())))
            );
        }
    }

    private static Stream<Arguments> provideUsersWithSingleNonValidAttribute() {
        refUser = getTestUser();
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
}
