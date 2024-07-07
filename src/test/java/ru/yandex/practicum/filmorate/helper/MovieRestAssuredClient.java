package ru.yandex.practicum.filmorate.helper;

import io.restassured.response.Response;
import ru.yandex.practicum.filmorate.model.Movie;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.fail;


public class MovieRestAssuredClient extends BaseRestAssuredClient {

    public MovieRestAssuredClient() {
        super("/films");
    }

    public Response sendPostRequest(Movie movie) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .accept(JSON)
                    .body(movie)
                    .post(urlPrefix);
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
                    .get(urlPrefix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendPutRequest(Movie movie) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .body(movie)
                    .put(urlPrefix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }
}
