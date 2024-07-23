package ru.yandex.practicum.filmorate.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.List;


@DisplayName("Отметки \"Нравится\" фильмов")
public class FilmLikesTests  extends BaseTest {

    private static final RestAssuredClient filmClient = new RestAssuredClient("/films");
    private static final RestAssuredClient userClient = new RestAssuredClient("/users");
    private static final List<User> users = new ArrayList<>();

    @LocalServerPort
    int port;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void beforeAll() {
        for (int i = 0; i < 20; i++) {
            userClient.sendPost(getTestUser())
                    .then()
                    .extract()
                    .as(User.class);
        }
    }
}
