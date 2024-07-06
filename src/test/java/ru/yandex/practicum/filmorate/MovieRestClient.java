package ru.yandex.practicum.filmorate;

import io.restassured.response.Response;
import ru.yandex.practicum.filmorate.model.Movie;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.*;
import static org.junit.jupiter.api.Assertions.fail;


public class MovieRestClient extends BaseRestClient {

    public Response sendCreateMovieRequest(Movie movie) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .accept(JSON)
                    .body(movie)
                    .post("/movie");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendGetAllMoviesRequest() {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .accept(JSON)
                    .get("/movie/all");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendPutMovieRequest(Movie movie) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .body(movie)
                    .put("/movie/" + movie.getId());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }
}
