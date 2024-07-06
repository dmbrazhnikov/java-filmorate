package ru.yandex.practicum.filmorate.helper;

import io.restassured.response.Response;
import ru.yandex.practicum.filmorate.model.User;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.fail;

public class UserRestAssuredClient extends BaseRestAssuredClient {

    public UserRestAssuredClient() {
        super("/user");
    }

    public Response sendPostRequest(User user) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .accept(JSON)
                    .body(user)
                    .post(URL_PREFIX);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendGetAllRequest() {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .accept(JSON)
                    .get(URL_PREFIX + "/all");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendPutRequest(User user) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .body(user)
                    .put(URL_PREFIX + "/" + user.getId());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }
}
