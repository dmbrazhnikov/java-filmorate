package ru.yandex.practicum.filmorate.helper;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.fail;


public abstract class BaseRestAssuredClient {

    protected final String urlPrefix;
    protected static RestAssuredConfig config;

    public BaseRestAssuredClient(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 10000)
                        .setParam("http.connection.timeout", 10000));
    }

    public <T> Response sendPostRequest(T payload) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .accept(JSON)
                    .body(payload)
                    .post(urlPrefix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public <T> Response sendPutRequest(T payload) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .body(payload)
                    .put(urlPrefix);
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
}
