package ru.yandex.practicum.filmorate.test.e2e;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.fail;


public class RestAssuredClient {

    protected final String urlPrefix;
    protected static RestAssuredConfig config;

    public RestAssuredClient(String urlPrefix) {
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

    public Response sendGet(String uriSuffix) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .accept(JSON)
                    .get(urlPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }
}
